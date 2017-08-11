package com.luffyjet.gweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Title :
 * Author : luffyjet
 * Date : 2017/5/12
 * Project : gweb001
 * Site : http://www.luffyjet.com
 */
@Controller
public class IndexController {
    @RequestMapping("/")
    public String index() {
        return "views/signature.html";
    }
}
