package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import jsons.Course;
import play.libs.Json;
import play.libs.WS;
import play.mvc.Result;

import static play.libs.F.Function;
import static play.libs.F.Promise;
import static play.libs.Json.toJson;

import play.mvc.*;

import java.util.*;
import java.util.zip.GZIPInputStream;

public class Coursera extends Controller {
    static final String uri = "https://www.coursera.org/maestro/api/topic/list2?unis=id,name&topics=id,name,small_icon&cats=id,name&insts=id&courses=id,topic_id,home_link";

    public static Promise<Result> index(final String category) {
        final Promise<Result> resultPromise = WS.url(uri)
                .setHeader("Accept-Encoding", "gzip")
                .setHeader("User-Agent", "Mozilla")
                .get()
                .map(
                    new Function<WS.Response, Result>() {
                        public Result apply(WS.Response response) throws Exception {
                            GZIPInputStream stream = new GZIPInputStream(response.getBodyAsStream());
                            JsonNode jsonNode = Json.parse(stream);

                            //search categoryId
                            int categoryId = -1;
                            JsonNode catsNodes = jsonNode.path("cats");
                            for (JsonNode node : catsNodes) {
                                String name = node.findValue("name").asText();
                                if (name.compareToIgnoreCase(category) == 0) {
                                    categoryId = node.findValue("id").asInt();
                                    break;
                                }
                            }

                            if (categoryId == -1) {
                                throw new Exception("Category is not found");
                            }

                            //get universities
                            JsonNode unisNodes = jsonNode.path("unis");
                            Dictionary<Integer, String> universities = new Hashtable<>();
                            for (JsonNode node : unisNodes) {
                                universities.put(node.path("id").asInt(), node.path("name").asText());
                            }

                            //get topics
                            JsonNode topicsNodes = jsonNode.path("topics");
                            Dictionary<Integer, Course> topics = new Hashtable<>();
                            for (JsonNode node : topicsNodes) {
                                JsonNode cats = node.path("cats");
                                for (JsonNode cat : cats) {
                                    if (categoryId == cat.asInt()) {
                                        Course course = new Course();
                                        course.topicId = node.path("id").asInt();
                                        course.name = node.path("name").asText();
                                        course.image = node.path("small_icon").asText();

                                        //get university names
                                        JsonNode unis = node.path("unis");
                                        StringBuilder sb = new StringBuilder();
                                        for (JsonNode u : unis) {
                                            String universityName = universities.get(u.asInt());
                                            sb.append(universityName).append(", ");
                                        }
                                        sb.setLength(sb.length() - 2);
                                        course.university = sb.toString();

                                        topics.put(course.topicId, course);
                                        break;
                                    }
                                }
                            }

                            //get courses
                            JsonNode coursesNodes = jsonNode.path("courses");
                            for (JsonNode node : coursesNodes) {
                                int topic_id = node.path("topic_id").asInt();

                                Course course = topics.get(topic_id);
                                if (course != null)
                                    course.uri = node.path("home_link").asText();
                            }
                            return ok(toJson(topics.elements()));
                        }
                    }
                );
        return resultPromise;
    }
}
