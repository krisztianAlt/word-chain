package com.wordchain.controller;

import com.wordchain.controller.collectData.OnlineEntitiesDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class OnlineEntitiesController {

    @Autowired
    OnlineEntitiesDataHandler onlineEntitiesDataHandler;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listingOnlineEntities(@RequestParam Map<String,String> allRequestParams,
                                        Model model,
                                        HttpServletRequest httpServletRequest) {
        onlineEntitiesDataHandler.collectOnlineEntitiesData(allRequestParams, model, httpServletRequest);
        return "index";
    }



}
