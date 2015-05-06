#!/usr/bin/env python
import sys
import tweepy
# Authentification oAuth
CONSUMER_KEY = '0PqersXnGOn7AirB13jszw'
CONSUMER_SECRET = 'r3qyCuhmwRI90JY04kqcYtn5yEl6yaW1UoRhAY08Q'
ACCESS_KEY = '1598158057-epoKcN3Lfz405zjNZQ2rl5cx74lthvBZ93cCsfj'
ACCESS_SECRET = '6YwDPdjS7OpqDHb6PsmW0beOjCgjITzDGYV89gtQU'
auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
auth.set_access_token(ACCESS_KEY, ACCESS_SECRET)
api = tweepy.API(auth)
api.update_status(sys.argv[1])
