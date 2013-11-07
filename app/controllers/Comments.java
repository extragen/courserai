package controllers;

import play.data.Form;
import play.mvc.*;

import static play.libs.Json.toJson;

public class Comments extends Controller {

    public static Result index(int topicId) {
        return ok(toJson(models.Comment.allTopicComments(topicId)));
    }

    public static Result add() {
        Form<models.Comment> filledForm = Form.form(models.Comment.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest();
        }

        models.Comment comment = filledForm.get();
        comment.save();

        return created();
    }
}
