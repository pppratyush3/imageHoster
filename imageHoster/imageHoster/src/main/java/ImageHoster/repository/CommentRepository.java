package ImageHoster.repository;

import ImageHoster.model.Comment;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;

@Repository
public class CommentRepository {

    @PersistenceUnit(unitName = "imageHoster")
    private EntityManagerFactory emf;

    // this method makes the comment into the database, starts a transaction and is commited if the transaction is successful and
    // if transaction fails, it rollsback to the previous transaction
    public void createComment(Comment comment){
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(comment);
            transaction.commit();
        }
        catch (Exception e) {
            transaction.rollback();
        }
    }
    // getting all the comments of a particular Image (imageId) and fetches the detail from the database and return a list of comments
    public List<Comment> getComments(Integer imageId) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Comment> query = em.createQuery("select c from Comment c where c.image.id = :imageId", Comment.class).setParameter("imageId", imageId);

        List<Comment> comments = query.getResultList();
        return comments;
    }

}
