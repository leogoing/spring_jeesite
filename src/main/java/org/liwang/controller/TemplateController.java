package org.liwang.controller;

import org.liwang.commen.controller.CommonLController;
import org.liwang.entity.Template;
import org.liwang.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 模板
 * @author liwng
 *
 */
@Controller("template")
@RequestMapping("${adminPath}/gold/template")
public class TemplateController extends CommonLController<TemplateService, Template>{

}
