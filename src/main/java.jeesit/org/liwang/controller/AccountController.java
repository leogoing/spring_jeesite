package org.liwang.controller;

import org.liwang.commen.controller.CommonLController;
import org.liwang.entity.Account;
import org.liwang.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 流水账
 * @author liwang
 *
 */
@Controller
@RequestMapping("${adminPath}/gold/account")
public class AccountController extends CommonLController<AccountService, Account>{

}
