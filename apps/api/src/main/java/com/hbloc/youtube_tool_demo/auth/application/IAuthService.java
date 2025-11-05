package com.hbloc.youtube_tool_demo.auth.application;

public interface IAuthService {
    String register(String email, String password);
    String login(String email, String password);

}
