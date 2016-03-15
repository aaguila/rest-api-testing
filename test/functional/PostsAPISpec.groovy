import net.hedtech.restfulapi.spock.*

import grails.converters.JSON
import spock.lang.Unroll
import constants.Headers
import constants.ResponseStatus
import constants.TestConfig


/**
 * Specs for testing Rest API.
 *
 */
@Unroll
class PostsAPISpec extends RestSpecification{

    private static final String PATH = "${TestConfig.BASE_URL}/posts"

    def "check that the API returns all posts data"() {

        when: "make a get request to the api"

        get("${PATH}") {
            headers[Headers.CONTENT_TYPE] = TestConfig.CONTENT_TYPE
            headers[Headers.ACCEPT] = TestConfig.CONTENT_TYPE
        }

        then: "a json is received with the desired posts"

        //Headers information assertion
        response.status == ResponseStatus.GET_STATUS_OK
        responseHeader(Headers.ACCESS_CONTROL_ALLOW_CREDENTIALS) == "true"
        responseHeader(Headers.CONTENT_TYPE_OPTIONS) == "nosniff"
        responseHeader(Headers.POWERED) == "Express"
        responseHeader(Headers.SERVER) == "Cowboy"

        //Json information assertion
        def json = JSON.parse response.text
        json.size() > 0

    }

    def "check that the API returns #postId post data"(int postId) {

        when: "make a get request to the api with post id"

        get("${PATH}/${postId}") {
            headers[Headers.CONTENT_TYPE] = TestConfig.CONTENT_TYPE
            headers[Headers.ACCEPT] = TestConfig.CONTENT_TYPE
        }

        then: "a json is received with the desired post data"

        //Headers information assertion
        response.status == ResponseStatus.GET_STATUS_OK
        responseHeader(Headers.ACCESS_CONTROL_ALLOW_CREDENTIALS) == "true"
        responseHeader(Headers.CONTENT_TYPE_OPTIONS) == "nosniff"
        responseHeader(Headers.POWERED) == "Express"
        responseHeader(Headers.SERVER) == "Cowboy"

        //Json information assertion
        def json = JSON.parse response.text
        json.size() > 0
        json.id == postId
        !json.title.allWhitespace
        !json.body.allWhitespace

        where:
        postId << [1, 2, 3]
    }

    def "check that the API does not returns data for not existing #postId post"(int postId) {

        when: "make a get request to the api with post id"

        get("${PATH}/${postId}") {
            headers[Headers.CONTENT_TYPE] = TestConfig.CONTENT_TYPE
            headers[Headers.ACCEPT] = TestConfig.CONTENT_TYPE
        }

        then: "a json is received with the desired post data"

        //Headers information assertion
        response.status == ResponseStatus.GET_STATUS_KO
        responseHeader(Headers.ACCESS_CONTROL_ALLOW_CREDENTIALS) == "true"
        responseHeader(Headers.CONTENT_TYPE_OPTIONS) == "nosniff"
        responseHeader(Headers.POWERED) == "Express"
        responseHeader(Headers.SERVER) == "Cowboy"

        //Json information assertion
        def json = JSON.parse response.text
        json.size() == 0

        where:
        postId << [2000, 3000, 4000]
    }

    def "check that the API returns #userId posts data"(int userId) {

        when: "make a get request to the api with user id"

        get("${PATH}?userId=${userId}") {
            headers[Headers.CONTENT_TYPE] = TestConfig.CONTENT_TYPE
            headers[Headers.ACCEPT] = TestConfig.CONTENT_TYPE
        }

        then: "a json is received with the desired post data"

        //Headers information assertion
        response.status == ResponseStatus.GET_STATUS_OK
        responseHeader(Headers.ACCESS_CONTROL_ALLOW_CREDENTIALS) == "true"
        responseHeader(Headers.CONTENT_TYPE_OPTIONS) == "nosniff"
        responseHeader(Headers.POWERED) == "Express"
        responseHeader(Headers.SERVER) == "Cowboy"

        //Json information assertion
        def json = JSON.parse response.text
        json.size() > 0
        json.each { post ->
            post.userId == userId
            !post.title.allWhitespace
            !post.body.allWhitespace
        }

        where:
        userId << [1, 2, 3]
    }

    def "check that we can post a posts with the API"(String userId, String title, String text) {

        when: "make a post request to the api"

        post("${PATH}") {
            headers[Headers.CONTENT_TYPE] = TestConfig.CONTENT_TYPE
            headers[Headers.ACCEPT] = TestConfig.CONTENT_TYPE
            body {
                """
                {
                    "userId": "${userId}",
                    "title": "${title}",
                    "body": "${text}"
                }
                """
            }
        }

        then: "a json is received with the desired data"

        //Headers information assertion
        response.status == ResponseStatus.POST_STATUS_OK
        responseHeader(Headers.ACCESS_CONTROL_ALLOW_CREDENTIALS) == "true"
        responseHeader(Headers.CONTENT_TYPE_OPTIONS) == "nosniff"
        responseHeader(Headers.POWERED) == "Express"
        responseHeader(Headers.SERVER) == "Cowboy"

        //Json information assertion
        def json = JSON.parse response.text
        json.size() > 1
        json.id != 0

        where:

        userId | title         | text
        "1"    | "test_title1" | "test_body1"
        "2"    | "test_title2" | "test_body2"
        "3"    | "test_title3" | "test_body3"

    }

    def "check that we can edit posts with the API"(String postId, String title, String text) {

        when: "make a put request to the api"

        put("${PATH}/${postId}") {
            headers[Headers.CONTENT_TYPE] = TestConfig.CONTENT_TYPE
            headers[Headers.ACCEPT] = TestConfig.CONTENT_TYPE
            body {
                """
                {
                    "title": "${title}",
                    "body": "${text}"
                }
                """
            }
        }

        then: "a json is received with the desired data"

        //Headers information assertions
        response.status == ResponseStatus.GET_STATUS_OK
        responseHeader(Headers.ACCESS_CONTROL_ALLOW_CREDENTIALS) == "true"
        responseHeader(Headers.CONTENT_TYPE_OPTIONS) == "nosniff"
        responseHeader(Headers.POWERED) == "Express"
        responseHeader(Headers.SERVER) == "Cowboy"

        //Json information assertion
        def json = JSON.parse response.text
        json.size() > 1
        json.id == postId as int

        where:

        postId | title         | text
        "1"    | "test_title1" | "test_body1"
        "2"    | "test_title2" | "test_body2"
        "3"    | "test_title3" | "test_body3"

    }

}
