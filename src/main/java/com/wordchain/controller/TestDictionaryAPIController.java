package com.wordchain.controller;

import com.wordchain.service.DictionaryApiCalling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TestDictionaryAPIController {

    @Autowired
    DictionaryApiCalling dictionaryApiCalling;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String testPage(Model model) {
        model.addAttribute("word", "");
        model.addAttribute("meanings", new ArrayList<>());
        model.addAttribute("checking", false);
        return "dictionary-test";
    }

    @RequestMapping(value = "/word-check", method = RequestMethod.POST)
    public String checkWord(@RequestParam("word") String word,
                            Model model){
        List<String> meanings = dictionaryApiCalling.getMeanings(word);
        model.addAttribute("word", word);
        model.addAttribute("meanings", meanings);
        model.addAttribute("checking", true);
        return "dictionary-test";
    }

}
