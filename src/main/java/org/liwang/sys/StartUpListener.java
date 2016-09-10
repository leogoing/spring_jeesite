package org.liwang.sys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * spring启动后触发加载
 * @author liwang
 *
 */
@Component
public class StartUpListener implements ApplicationListener<ContextRefreshedEvent>{

	private static final Logger log=LoggerFactory.getLogger(StartUpListener.class);
	
	/**
	 * 在所有应用容器初始化完成后都会调用
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent() == null){//只在根容器加载完成后加载加载
			log.debug("spring init over...");
		}
		
	}

}
