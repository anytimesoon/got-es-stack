package xyz.delartigue.gotdemo.services

import org.springframework.stereotype.Service
import xyz.delartigue.gotdemo.data.ElasticSearchClient

@Service
class SearchService (
    private val elasticSearchClient: ElasticSearchClient
) {
    fun getSearchResult(query: String) = elasticSearchClient.query(query).body?.string() ?: ""
}