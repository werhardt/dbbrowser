# DbBrowser

DbBrowser is a Play! application build on top of the [Scalongo Activator seed](https://github.com/iozozturk/scalongo#master).
The application extends the Scalongo template with a frontend for the login part (Authentication). You can create new
connections to your local Mongo database and browse through the collections and documents (incl. simple document filter).

Signup has to be done via REST call, see [sample request](https://github.com/iozozturk/scalongo#sample-requests).

Requests to restricted URLs are prevented with a SecureAction (Authorization).

Technologies:
* Play! Framework (2.5)
  * Forms
  * Authentication, Authorization
* MongoDb
  * official MongoDb Scala driver [Getting Started](http://mongodb.github.io/mongo-scala-driver/1.0/getting-started)
  * reactive access
* Twitter Bootstrap 3 (3.3.6) with [Twitter Bootstrap Helper Library](https://github.com/adrianhurt/play-bootstrap)

**Warning** Don't use this application in a production environment, since it exposes your database.
It is a sample project showcasing the Play! framework.
