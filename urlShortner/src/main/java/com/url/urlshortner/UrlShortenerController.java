package com.url.urlshortner;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpServletResponse;

import static com.url.urlshortner.Util.*;

@SuppressWarnings("unused")
@Controller
public class UrlShortenerController {

    private final ConcurrentHashMap<String, String> urlDatabase = new ConcurrentHashMap<>();

    @GetMapping("/")
    public String showForm() {
        return "index";
    }

    @PostMapping("/shorten")
    public String shortenUrl(@RequestParam("longUrl") String longUrl, Model model) {
        String shortCode = Util.generateShortCodeFromUrl(longUrl);
        urlDatabase.put(shortCode, longUrl);
        String shortUrl = "/" + shortCode;
        model.addAttribute("shortUrl", shortUrl);
        return "index";
    }

    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) {
        String longUrl = urlDatabase.get(shortCode);
        sleep(2000);
        if (longUrl != null) {
            sendRedirect(longUrl, response);
        } else {
            sendError(HttpServletResponse.SC_NOT_FOUND, "URL not found", response);
        }
    }

    @ResponseBody
    @GetMapping("/api/urls")
    public ResponseEntity<Map<String, String>> getAllUrls() {
        return ResponseEntity.ok(urlDatabase);
    }

    @ResponseBody
    @DeleteMapping("/api/urls")
    public ResponseEntity<String> deleteAllUrls() {
        urlDatabase.clear();
        return ResponseEntity.ok("All URLs deleted.");
    }
}
