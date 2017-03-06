package org.liwang.controller;

import org.liwang.commen.controller.CommonLController;
import org.liwang.entity.Counterparty;
import org.liwang.service.CounterpartyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 交易机构controller
 * @author liwang
 *
 */
@Controller
@RequestMapping("${adminPath}/gold/counterparty")
public class CounterpartyController extends CommonLController<CounterpartyService, Counterparty>{

}
