# Mobile_Cloud_Final_OSU
My completed project for my Mobile and Cloud Programming class. I built a very simple Android application that can talk to an API I created. 

Cloud Backend:
The backend for this project supports two entities, Pokemon and Trainers. These belong to the Pokemon universe and are used as sample data to show that entities may be added, deleted, edited, and replaced. Here is a list of URLs that were supported when the Google App Engine backend was up and running. This code may be forked and tested using local host.

1.	https://cs496-final-160904.appspot.com
  a.	GET: basic front page with welcome
  b.	DELETE: delete all Pokemon and Trainer entities
2.	https://cs496-final-160904.appspot.com/pokemon
  a.	GET: Receive all Pokemon Entities as a JSON array
  b.	POST: Add a new Pokemon Entity to the cloud.
  c.	DELETE: Delete all Pokemon Entities in the cloud.
3.	https://cs496-final-160904.appspot.com/pokemon/<pokemon_id>
  a.	GET: Receive information about that specific pokemon
  b.	PUT:  Update or overwrite attributes about a specific pokemon.
  c.	PATCH: Edit certain attributes about that pokemon
  d.	DELETE: Remove that pokemon from the database
4.	https://cs496-final-160904.appspot.com/pokemon?is_captured=[True|False]
  a.	GET: Returns all pokemon that are captured or not captured.
5.	https://cs496-final-160904.appspot.com/trainers
  a.	GET: Receive all Trainer Entities as a JSON array
  b.	POST: Add a new Trainer Entity to the cloud.
  c.	DELETE: Delete all Trainer Entities in the cloud.
6.	https://cs496-final-160904.appspot.com/trainers/<trainer_id>
  a.	GET: Receive information about that specific trainer
  b.	PUT:  Update or overwrite attributes about a specific trainer.
  c.	PATCH: Edit certain attributes about that trainer
  d.	DELETE: Remove that trainer from the database
7.	https://cs496-final-160904.appspot.com/trainers/<trainer_id>/pokemon
  a.	GET: Receive all Pokemon Entities as a JSON array that belong to <trainer_id>.
8.	https://cs496-final-160904.appspot.com/trainers/<trainer_id>/pokemon/<pokemon_id>
  a.	GET: Receive information about that specific pokemon that belongs to <trainer_id>
  b.	PUT:  Adds this pokemon to the trainer’s list of captured pokemon
  c.	DELETE: Remove that pokemon from the trainer’s list of pokemon.

Android application:
A simple Android application that can make GET, POST, PUT, and DELETE requests to the backend. 

