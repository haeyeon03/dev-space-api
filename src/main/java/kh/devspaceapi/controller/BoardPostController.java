package kh.devspaceapi.controller;

import kh.devspaceapi.service.BoardPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/board-posts")
@RestController
public class BoardPostController {
    @Autowired
    private BoardPostService boardPostService;
}
