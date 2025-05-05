package xyz.delartigue.gotdemo.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.delartigue.gotdemo.services.SearchService

@RestController
@RequestMapping("/search")
class SearchController(
    private val searchService: SearchService
) {
    @GetMapping("")
    fun getSearchResults(
        @RequestParam(name = "q", required = true) query: String,
    ): String {
        return searchService.getSearchResult(query)
    }
}