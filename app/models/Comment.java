package models;

import play.data.validation.*;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Comment extends Model {

    @Id
    public int id;

    @Constraints.Required
    public int topicId;

    public String email;

    @Constraints.Required
    public String message;

    public static Finder<Long,Comment> find = new Finder (
            Long.class, Comment.class
    );

    public static List<Comment> allTopicComments(int topicId) {
        return find.where().eq("topicId", topicId).findList();
    }
}
