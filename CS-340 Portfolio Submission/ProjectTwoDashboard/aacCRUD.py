from pymongo import MongoClient
from bson.objectid import ObjectId


class AnimalShelter(object):
    '''CRUD operations for Animal collection in MongoDB'''

    def __init__(self, username, password):
        # Connection Variables
        USER = username
        PASS = password
        HOST = 'nv-desktop-services.apporto.com'
        PORT = 32552
        DB = 'AAC'
        COL = 'animals'

        # Initialize the DB connection
        self.client = MongoClient(f'mongodb://{USER}:{PASS}@{HOST}:{PORT}')
        self.database = self.client[DB]
        self.collection = self.database[COL]

    def create(self, data):
        '''Create a new document in the collection'''
        if data:
            result = self.collection.insert_one(data)
            # Check if the insert succeeded
            if result.acknowledged:
                return True
            else:
                return False
        else:
            raise Exception("Empty data parameter provided. Nothing to write.")

    def read(self, query):
        '''Read matching documents from the collection'''
        try:
            cursor = self.collection.find(query)
            results = list(cursor)
            return results
        except Exception as e:
            print(f'{e}')
            return []

    def update(self, query, new_data):
        '''Update documents matching <query> with all key:value pairs in <new_data>'''
        try:
            result = self.collection.update_many(query, {"$set": new_data})
            return result.modified_count
        except Exception as e:
            print(f'{e}')
            return 0

    def delete(self, query):
        '''Delete documents matching <query> from the collection'''
        try:
            result = self.collection.delete_many(query)
            return result.deleted_count
        except Exception as e:
            print(f'{e}')
            return 0
