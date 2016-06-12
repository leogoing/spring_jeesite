/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.util.ResourceUtils;

/**
 * 继承AbstractResource在其对Resource接口的基本实现的基础上有了更具体得实现(主要多了URL资源的判断)<p>
 * Abstract base class for resources which resolve URLs into File references,
 * such as {@link UrlResource} or {@link ClassPathResource}.
 *
 * <p>Detects the "file" protocol as well as the JBoss "vfs" protocol in URLs,
 * resolving file system references accordingly.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public abstract class AbstractFileResolvingResource extends AbstractResource {

	/**
	 * 先获取该资源的URL再根据URL转换为file返回<p>
	 * This implementation returns a File reference for the underlying class path
	 * resource, provided that it refers to a file in the file system.
	 * @see org.springframework.util.ResourceUtils#getFile(java.net.URL, String)
	 */
	@Override
	public File getFile() throws IOException {
		URL url = getURL();
		if (url.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {//如果url的协议是"vfs"开头(JBoss)
			return VfsResourceDelegate.getResource(url).getFile();
		}
		return ResourceUtils.getFile(url, getDescription());
	}

	/**
	 * 获取实际的文件,如资源在一个jar包中<p>
	 * This implementation determines the underlying File
	 * (or jar file, in case of a resource in a jar/zip).
	 */
	@Override
	protected File getFileForLastModifiedCheck() throws IOException {
		URL url = getURL();
		if (ResourceUtils.isJarURL(url)) {//是否属于jar包的URL
			URL actualUrl = ResourceUtils.extractJarFileURL(url);
			if (actualUrl.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
				return VfsResourceDelegate.getResource(actualUrl).getFile();
			}
			return ResourceUtils.getFile(actualUrl, "Jar URL");
		}
		else {
			return getFile();
		}
	}

	/**
	 * 根据URI获取文件<p>
	 * This implementation returns a File reference for the underlying class path
	 * resource, provided that it refers to a file in the file system.
	 * @see org.springframework.util.ResourceUtils#getFile(java.net.URI, String)
	 */
	protected File getFile(URI uri) throws IOException {
		if (uri.getScheme().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
			return VfsResourceDelegate.getResource(uri).getFile();
		}
		return ResourceUtils.getFile(uri, getDescription());
	}


	/**
	 * 判断文件是否存在或能否打开流或URL连接是否成功
	 */
	@Override
	public boolean exists() {
		try {
			URL url = getURL();
			if (ResourceUtils.isFileURL(url)) {
				// Proceed with file system resolution...
				return getFile().exists();
			}
			else {
				// Try a URL connection content-length header...
				URLConnection con = url.openConnection();
				customizeConnection(con);
				HttpURLConnection httpCon =
						(con instanceof HttpURLConnection ? (HttpURLConnection) con : null);
				if (httpCon != null) {//若为Http连接检查响应是否正常
					int code = httpCon.getResponseCode();//获取响应状态码
					if (code == HttpURLConnection.HTTP_OK) {
						return true;
					}
					else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
						return false;
					}
				}
				if (con.getContentLength() >= 0) {//若为普通连接检查是否有内容长度
					return true;
				}
				if (httpCon != null) {//连接失败,关闭连接
					// no HTTP OK status, and no content-length header: give up
					httpCon.disconnect();
					return false;
				}
				else {//检查是否能打开流
					// Fall back to stream existence: can we open the stream?
					InputStream is = getInputStream();
					is.close();
					return true;
				}
			}
		}
		catch (IOException ex) {
			return false;
		}
	}

	/**
	 * 若能获取URL则为可读,若URL为文件资源则判断文件是否可读
	 */
	@Override
	public boolean isReadable() {
		try {
			URL url = getURL();
			if (ResourceUtils.isFileURL(url)) {
				// Proceed with file system resolution...
				File file = getFile();
				return (file.canRead() && !file.isDirectory());
			}
			else {
				return true;
			}
		}
		catch (IOException ex) {
			return false;
		}
	}

	/**
	 * 若为文件则获取文件内容字符长度,若为连接则获取连接后的内容长度
	 */
	@Override
	public long contentLength() throws IOException {
		URL url = getURL();
		if (ResourceUtils.isFileURL(url)) {
			// Proceed with file system resolution...
			return getFile().length();
		}
		else {
			// Try a URL connection content-length header...
			URLConnection con = url.openConnection();
			customizeConnection(con);
			return con.getContentLength();
		}
	}

	/**
	 * 若为文件则获取文件最后修改时间戳,否则建立URL连接获取连接信息中的最后修改时间
	 */
	@Override
	public long lastModified() throws IOException {
		URL url = getURL();
		if (ResourceUtils.isFileURL(url) || ResourceUtils.isJarURL(url)) {
			// Proceed with file system resolution...
			return super.lastModified();
		}
		else {
			// Try a URL connection last-modified header...
			URLConnection con = url.openConnection();
			customizeConnection(con);
			return con.getLastModified();
		}
	}


	/**
	 * 如果必要先添加连接缓存,若为HttpURL连接设置连接请求方法为"HEAD"<p>
	 * Customize the given {@link URLConnection}, obtained in the course of an
	 * {@link #exists()}, {@link #contentLength()} or {@link #lastModified()} call.
	 * <p>Calls {@link ResourceUtils#useCachesIfNecessary(URLConnection)} and
	 * delegates to {@link #customizeConnection(HttpURLConnection)} if possible.
	 * Can be overridden in subclasses.
	 * @param con the URLConnection to customize
	 * @throws IOException if thrown from URLConnection methods
	 */
	protected void customizeConnection(URLConnection con) throws IOException {
		ResourceUtils.useCachesIfNecessary(con);
		if (con instanceof HttpURLConnection) {
			customizeConnection((HttpURLConnection) con);
		}
	}

	/**
	 * 设置URL连接请求方法为"HEAD"<p>
	 * Customize the given {@link HttpURLConnection}, obtained in the course of an
	 * {@link #exists()}, {@link #contentLength()} or {@link #lastModified()} call.
	 * <p>Sets request method "HEAD" by default. Can be overridden in subclasses.
	 * @param con the HttpURLConnection to customize
	 * @throws IOException if thrown from HttpURLConnection methods
	 */
	protected void customizeConnection(HttpURLConnection con) throws IOException {
		con.setRequestMethod("HEAD");
	}


	/**
	 * 避免JBoss VFS API在运行时依赖<p>
	 * Inner delegate class, avoiding a hard JBoss VFS API dependency at runtime.
	 */
	private static class VfsResourceDelegate {

		public static Resource getResource(URL url) throws IOException {
			return new VfsResource(VfsUtils.getRoot(url));
		}

		public static Resource getResource(URI uri) throws IOException {
			return new VfsResource(VfsUtils.getRoot(uri));
		}
	}

}
