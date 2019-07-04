// package capital.clix.los.repository;
//
// import java.util.List;
// import org.springframework.data.couchbase.core.query.Query;
// import org.springframework.data.couchbase.core.query.ViewIndexed;
// import org.springframework.data.couchbase.repository.CouchbaseRepository;
//
// @ViewIndexed(designDoc = "person", viewName = "all")
// public interface PersonRepository extends CouchbaseRepository<Person, String> {
// List<Person> findByFirstName(String firstName);
//
// @Query("#{#n1ql.selectEntity} ")
// List<Person> findAllAdmins();
// }
