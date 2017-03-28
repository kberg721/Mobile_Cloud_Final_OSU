#!/opt/local/bin/python2.7
"""
Kyle Bergman
cs496
Final Project
Winter '17'
"""
import webapp2
import json
from google.appengine.ext import ndb


class Trainer(ndb.Model):
    """
    A model to describe a Trainer in the Pokemon universe.
    """
    name = ndb.StringProperty(required=True)
    hometown = ndb.StringProperty()
    num_badges = ndb.IntegerProperty()
    pokemon_caught = ndb.StringProperty(repeated=True)


class Pokemon(ndb.Model):
    """
    A model to describe a Pokemon in the Pokemon universe.
    """
    name = ndb.StringProperty(required=True)
    trainer = ndb.StringProperty(default="")
    type = ndb.StringProperty(repeated=True)
    is_captured = ndb.BooleanProperty(default=False)
    height = ndb.FloatProperty()
    weight = ndb.FloatProperty()


class TrainerHandler(webapp2.RequestHandler):
    """
    A handler to complete transactions such as listing, adding, updating, and deleting trainers.
    """

    def get(self, trainer_id=None):
        """
        Retrieves information about a single trainer or all the trainers.
        :param trainer_id: Optional ID for a single trainer
        :return: JSON content about desired trainer(s)
        """

        # if we want a single trainer
        if trainer_id:
            # retrieve info based off id
            trainer = ndb.Key(urlsafe=trainer_id).get()
            trainer_d = trainer.to_dict()

            # add self and id attributes
            trainer_d['self'] = "/trainers/" + trainer_id
            trainer_d['id'] = trainer.key.urlsafe()

            # write out json formatted text to user
            self.response.write(json.dumps(trainer_d))
        else:
            # query for all trainers
            ancestor_key = ndb.Key('Trainer', "parent_trainer")
            result = Trainer.query(ancestor=ancestor_key)

            # format all content with valid json, as an array
            to_return = ""
            for trainer in result.fetch():
                trainer_d = trainer.to_dict()
                trainer_d['self'] = "/trainers/" + trainer.key.urlsafe()
                to_return += json.dumps(trainer_d) + ", "

            # if we have at least one trainer, fix formatting
            if len(to_return) != 1:
                to_return = to_return[:-2]
            self.response.write("[" + to_return + "]")

    def post(self):
        """
        Adds a new trainer to the data store.
        """
        # Put trainer in ancestor group 'parent_trainer'
        parent_key = ndb.Key(Trainer, "parent_trainer")

        # Retrieve trainer info to be put in data store
        trainer_data = json.loads(self.request.body)

        # Update attributes of trainer entity based on content of body
        new_trainer = Trainer(name=trainer_data['name'], parent=parent_key)
        if trainer_data.get('hometown') is not None:
            new_trainer.hometown = trainer_data['hometown']
        if trainer_data.get('pokemon_caught') is not None:
            new_trainer.pokemon_caught = trainer_data['pokemon_caught']
        if trainer_data.get('num_badges') is not None:
            new_trainer.num_badges = trainer_data['num_badges']

        # Put new trainer in data store
        new_trainer.put()

        # Format into easily digestible JSON and write to client
        trainer_dict = new_trainer.to_dict()
        trainer_dict['self'] = '/trainers/' + new_trainer.key.urlsafe()
        trainer_dict['id'] = new_trainer.key.urlsafe()
        self.response.set_status(201)
        self.response.write(json.dumps(trainer_dict))

    def put(self, trainer_id):
        """
        Overwrites or updates a trainer entity with the given trainer_id
        :param trainer_id: Where we want to store the updated/new trainer
        """

        # Retrieve new trainer attributes
        trainer_data = json.loads(self.request.body)

        # Retrieve trainer entity already associated with key
        trainer = ndb.Key(urlsafe=trainer_id).get()

        # Update necessary attributes
        trainer.name = trainer_data['name']
        if trainer_data.get('num_badges') is not None:
            trainer.num_badges = trainer_data['num_badges']
        else:
            trainer.num_badges = 0
        if trainer_data.get('pokemon_caught') is not None:
            trainer.pokemon_caught = trainer_data['pokemon_caught']
        else:
            trainer.pokemon_caught = []
        if trainer_data.get('hometown') is not None:
            trainer.hometown = trainer_data['hometown']
        else:
            trainer.num_badges = ""

        # Put updated trainer in data store
        trainer.put()

        # Format into JSON and add self + id attributes
        trainer_dict = trainer.to_dict()
        trainer_dict['self'] = '/trainers/' + trainer.key.urlsafe()
        trainer_dict['id'] = trainer.key.urlsafe()
        self.response.set_status(201)
        self.response.write(json.dumps(trainer_dict))

    def patch(self, trainer_id=None):
        """
        Updates a given trainer with the necessary attributes
        :param trainer_id: ID of the trainer we are updating.
        :return: JSON of updated trainer
        """

        # If we are given the right number of arguments
        if trainer_id:
            # Retrieve info to update and info currently stored
            trainer_data = json.loads(self.request.body)
            trainer = ndb.Key(urlsafe=trainer_id).get()

            # Update necessary attributes and put in data store
            if trainer_data.get('name') is not None:
                trainer.name = trainer_data['name']
            if trainer_data.get('num_badges') is not None:
                trainer.num_badges = trainer_data['num_badges']
            if trainer_data.get('pokemon_caught') is not None:
                trainer.pokemon_caught = trainer_data['pokemon_caught']
            if trainer_data.get('hometown') is not None:
                trainer.hometown = trainer_data['hometown']
            trainer.put()

            # Output new trainer to client
            trainer_d = trainer.to_dict()
            trainer_d['self'] = '/trainers/' + trainer_id
            trainer_d['id'] = trainer_id
            self.response.write(json.dumps(trainer_d))

    def delete(self, trainer_id=None):
        """
        Deletes one or all trainers from datastore.
        :param trainer_id: Optional ID of trainer to delete
        """

        # If we want to delete one trainer, we need an id
        if trainer_id:
            # Get trainer entity and delete
            trainer = ndb.Key(urlsafe=trainer_id).get()
            trainer.key.delete()
        else:
            # Otherwise, delete all trainer entities
            trainer_ancestor_key = ndb.Key('Trainer', "parent_trainer")
            result = Trainer.query(ancestor=trainer_ancestor_key)
            for trainer in result.fetch():
                trainer.key.delete()


class PokemonHandler(webapp2.RequestHandler):
    """
    A handler to complete transactions such as listing, adding, updating, and deleting Pokemon.
    """

    def get(self, pokemon_id=None):
        """
        Retrieves information about a single pokemon or all the pokemon.
        :param pokemon_id: Optional ID for a single pokemon
        :return: JSON content about desired pokemon(s)
        """

        # if we want a single pokemon
        if pokemon_id:
            # retrieve info based off id
            poke = ndb.Key(urlsafe=pokemon_id).get()
            poke_d = poke.to_dict()

            # add self and id attributes
            poke_d['self'] = "/pokemon/" + pokemon_id
            poke_d['id'] = poke.key.urlsafe()

            # write out json formatted text to user
            self.response.write(json.dumps(poke_d))
        else:
            # Get key of ancestor group
            ancestor_key = ndb.Key('Pokemon', "parent_pokemon")
            result = Pokemon.query(ancestor=ancestor_key)

            to_return = "["
            # If we want to know about is_captured status
            if self.request.get('is_captured'):
                status = self.request.get('is_captured')
                for pokemon in result.fetch():
                    pokemon_d = pokemon.to_dict()
                    if str(pokemon_d['is_captured']).lower() == status.lower():
                        pokemon_d['self'] = "/pokemon/" + pokemon.key.urlsafe()
                        pokemon_d['id'] = pokemon.key.urlsafe()
                        to_return += json.dumps(pokemon_d)
                        to_return += ", "

            # Otherwise, return all pokemon
            else:
                for pokemon in result.fetch():
                    pokemon_d = pokemon.to_dict()
                    pokemon_d['self'] = "/pokemon/" + pokemon.key.urlsafe()
                    to_return += json.dumps(pokemon_d)
                    to_return += ", "
            if len(to_return) != 1:
                to_return = to_return[:-2]
            self.response.write(to_return + "]")

    def post(self):
        """
        Adds a new pokemon to the data store.
        """
        # Put pokemon in ancestor group 'parent_pokemon'
        parent_key = ndb.Key(Pokemon, "parent_pokemon")

        # Retrieve pokemon info to be put in data store
        pokemon_data = json.loads(self.request.body)

        # Update attributes of pokemon entity based on content of body
        new_pokemon = Pokemon(name=pokemon_data['name'], parent=parent_key)
        if pokemon_data.get('type') is not None:
            new_pokemon.type = pokemon_data['type']
        if pokemon_data.get('is_captured') is not None:
            new_pokemon.is_captured = pokemon_data['is_captured']
        if pokemon_data.get('height') is not None:
            new_pokemon.height = pokemon_data['height']
        if pokemon_data.get('weight') is not None:
            new_pokemon.weight = pokemon_data['weight']

        # Put new pokemon in data store
        new_pokemon.put()

        # Format into easily digestible JSON and write to client
        pokemon_dict = new_pokemon.to_dict()
        pokemon_dict['self'] = '/pokemon/' + new_pokemon.key.urlsafe()
        pokemon_dict['id'] = new_pokemon.key.urlsafe()
        self.response.set_status(201)
        self.response.write(json.dumps(pokemon_dict))

    def put(self, pokemon_id):
        """
        Overwrites or updates a pokemon entity with the given pokemon_id
        :param pokemon_id: Where we want to store the updated/new pokemon
        """

        # Retrieve new pokemon attributes
        pokemon_data = json.loads(self.request.body)

        # Retrieve trainer entity already associated with key
        pokemon = ndb.Key(urlsafe=pokemon_id).get()

        # Update necessary attributes
        pokemon.name = pokemon_data['name']
        if pokemon_data.get('type') is not None:
            pokemon.type = pokemon_data['type']
        else:
            pokemon.type = []
        if pokemon_data.get('is_captured') is not None:
            pokemon.is_captured = pokemon_data['is_captured']
        else:
            pokemon.is_captured = False
        if pokemon_data.get('height') is not None:
            pokemon.height = pokemon_data['height']
        else:
            pokemon.height = 0.0
        if pokemon_data.get('weight') is not None:
            pokemon.weight = pokemon_data['weight']
        else:
            pokemon.weight = 0.0

        # Put updated pokemon in data store
        pokemon.put()

        # Format into JSON and add self + id attributes
        pokemon_dict = pokemon.to_dict()
        pokemon_dict['self'] = '/pokemon/' + pokemon.key.urlsafe()
        pokemon_dict['id'] = pokemon.key.urlsafe()
        self.response.set_status(201)
        self.response.write(json.dumps(pokemon_dict))

    def patch(self, pokemon_id=None):
        """
        Updates a given pokemon with the necessary attributes
        :param pokemon_id: ID of the pokemon we are updating.
        :return: JSON of updated pokemon
        """

        # If we are given the right number of arguments
        if pokemon_id:
            # Retrieve info to update and info currently stored
            pokemon_data = json.loads(self.request.body)
            pokemon = ndb.Key(urlsafe=pokemon_id).get()

            # Update necessary attributes and put in data store
            if pokemon_data.get('name') is not None:
                pokemon.name = pokemon_data['name']
            if pokemon_data.get('type') is not None:
                pokemon.type = pokemon_data['type']
            if pokemon_data.get('height') is not None:
                pokemon.height = pokemon_data['height']
            if pokemon_data.get('weight') is not None:
                pokemon.weight = pokemon_data['weight']
            if pokemon_data.get('is_captured') is not None:
                pokemon.is_captured = pokemon_data['is_captured']

            pokemon.put()

            # Output new trainer to client
            poke_d = pokemon.to_dict()
            poke_d['self'] = '/pokemon/' + pokemon_id
            poke_d['id'] = pokemon_id
            self.response.write(json.dumps(poke_d))

    def delete(self, pokemon_id=None):
        """
        Deletes one or all pokemon from datastore.
        :param pokemon_id: Optional ID of pokemon to delete
        """

        # If we want to delete one trainer, we need an id
        if pokemon_id:
            # Get pokemon entity and delete
            pokemon = ndb.Key(urlsafe=pokemon_id).get()
            if pokemon.is_captured is not True:
                pokemon.key.delete()
        else:
            # Otherwise, delete all pokemon entities
            pokemon_ancestor_key = ndb.Key('Pokemon', "parent_pokemon")
            result = Pokemon.query(ancestor=pokemon_ancestor_key)
            for pokemon in result.fetch():
                pokemon.key.delete()


class CapturedPokemonHandler(webapp2.RequestHandler):
    """
    A handler to take care of transactions dealing with pokemon captured by trainers.
    """

    def get(self, trainer_id=None, pokemon_id=None):
        """
        Get info about a certain pokemon or all pokemon captured by a specific trainer.
        :param trainer_id: id for the trainer
        :param pokemon_id: optional id for a specific pokemon
        :return: Information about the requested pokemon in JSON format
        """

        # If we are looking at a specific pokemon
        if trainer_id is not None and pokemon_id is not None:
            # Retrieve pokemon and trainer entities
            pokemon = ndb.Key(urlsafe=pokemon_id).get()
            pokemon_d = pokemon.to_dict()
            pokemon_d['self'] = "/pokemon/" + pokemon_id
            pokemon_d['id'] = pokemon_id
            trainer = ndb.Key(urlsafe=trainer_id).get()
            captured_pokemon = trainer.pokemon_caught
            # If the pokemon is indeed captured by the trainer, send back info
            for pokemon in captured_pokemon:
                if pokemon == pokemon_d['self']:
                    self.response.write(json.dumps(pokemon_d))

        # We want all pokemon captured by the trainer.
        elif trainer_id is not None:
            # Get key of ancestor group
            ancestor_key = ndb.Key('Pokemon', "parent_pokemon")
            result = Pokemon.query(ancestor=ancestor_key)

            to_return = "["
            for pokemon in result.fetch():
                pokemon_d = pokemon.to_dict()
                if str(pokemon_d['trainer']) == "/trainers/" + trainer_id:
                    pokemon_d['self'] = "/pokemon/" + pokemon.key.urlsafe()
                    pokemon_d['id'] = pokemon.key.urlsafe()
                    to_return += json.dumps(pokemon_d)
                    to_return += ", "
            if len(to_return) != 1:
                to_return = to_return[:-2]
            self.response.write(to_return + "]")

    def put(self, trainer_id=None, pokemon_id=None):
        """
        Adds a pokemon to a trainer's list of captured pokemon
        :param trainer_id: id of the trainer
        :param pokemon_id: id of the pokemon that was newly captured
        """

        # If we have the proper id arguments
        if trainer_id is not None and pokemon_id is not None:
            # Get trainer and pokemon entities
            trainer = ndb.Key(urlsafe=trainer_id).get()
            pokemon = ndb.Key(urlsafe=pokemon_id).get()

            # Make sure pokemon isn't caught already
            if pokemon.is_captured is False:
                # Update properties and append to pokemon_caught list of trainer
                pokemon.is_captured = True
                pokemon.trainer = "/trainers/" + trainer_id
                pokemon_dict = pokemon.to_dict()
                pokemon_dict['self'] = "/pokemon/" + pokemon_id
                trainer.pokemon_caught.append(pokemon_dict['self'])

                # Update entities and set code
                pokemon.put()
                trainer.put()
                self.response.set_status(201)

    def delete(self, trainer_id=None, pokemon_id=None):
        """
        Checks a pokemon back into the library
        :param trainer_id: id of customer releasing the pokemon
        :param pokemon_id: id of pokemon being released
        """
        if trainer_id is not None and pokemon_id is not None:
            # Get trainer and pokemon entities
            trainer = ndb.Key(urlsafe=trainer_id).get()
            pokemon = ndb.Key(urlsafe=pokemon_id).get()

            # If pokemon isn't already captured
            if pokemon.is_captured is True:
                # Check it in and update entities
                pokemon.is_captured = False
                pokemon.trainer = ""
                pokemon_dict = pokemon.to_dict()
                pokemon_dict['self'] = "/pokemon/" + pokemon_id
                trainer.pokemon_caught.remove(pokemon_dict['self'])
                pokemon.put()
                trainer.put()

class MainPage(webapp2.RequestHandler):
    """
    When the user goes to the root, they will encounter this page.
    """

    def get(self):
        """Output simple welcome message."""
        self.response.write("You've reached the OSU Pokemon Catalogue!")

    def delete(self):
        """
        Deletes all entities in the datastore.
        """
        # Get all pokemon and delete them.
        pokemon_ancestor_key = ndb.Key('Pokemon', "parent_pokemon")
        result = Pokemon.query(ancestor=pokemon_ancestor_key)
        for pokemon in result.fetch():
            pokemon.key.delete()

        # Get all customers and delete them.
        trainer_ancestor_key = ndb.Key('Trainer', "parent_trainer")
        result = Trainer.query(ancestor=trainer_ancestor_key)
        for trainer in result.fetch():
            trainer.key.delete()

        self.response.write("All Pokemon and Trainer entities have been removed.")


# Enable the Patch method to be used
allowed_methods = webapp2.WSGIApplication.allowed_methods
new_allowed_methods = allowed_methods.union(('PATCH',))
webapp2.WSGIApplication.allowed_methods = new_allowed_methods

# Implement the paths that will be used for this REST API
app = webapp2.WSGIApplication([
    webapp2.Route(r'/', handler=MainPage, name='main'),
    webapp2.Route(r'/pokemon', handler=PokemonHandler, name='pokemon'),
    webapp2.Route(r'/pokemon/<pokemon_id:.[^/]+>', handler=PokemonHandler, name='pokemon'),
    webapp2.Route(r'/pokemon?is_captured=<:[True|False|true|false]>', handler=PokemonHandler, name='captured/not_captured'),
    webapp2.Route(r'/trainers', handler=TrainerHandler, name='trainers'),
    webapp2.Route(r'/trainers/<trainer_id:.[^/]+>', handler=TrainerHandler, name='trainer'),
    webapp2.Route(r'/trainers/<trainer_id:.+>/pokemon', handler=CapturedPokemonHandler, name='capturedPokemon'),
    webapp2.Route(r'/trainers/<trainer_id:.+>/pokemon/<pokemon_id:.[^/]+>', handler=CapturedPokemonHandler, name='capturePokemon')
], debug=True)
