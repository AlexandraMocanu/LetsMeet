How to run:
1. Clone this repo and the one at https://github.com/vali-m/sma-back.git
2. Using Gradle, either
   2.1 Deploy application to emulator and deploy on the backend on the same machine
   2.2 Set `USES_EMULATOR` (RequestGateway:68) flag to false and the ip address (RequestGateway:74) of the machine with the backend
3. If you want to enable geolocation features, you need to add you own Google API key at AndroidManifest.xml:57

Request Gateway filepath = src\main\java\com\alexandra\sma_final\server

Let's Meet!

Android Application for meeting people.
Let's Meet! is the only Application which does not discriminate based on looks, race or gender.

Travelling to a new city? Find new friends to hang out with!

Solutie propusa (non tehnica)
Anonimat
* While the android app is completely anonymous, however the intended purpose excludes anonymity.
People (who enjoy chatting together) will inevitably meet, at which point the anonymity disappears.
We believe that anonymity is safeguards against discrimination and prejudice, and perhaps even
makes the users more sociable (not afraid of being judged).
Also, 

GeoLocatie 

* GeoLocation is essential to our app. If users are not close to each other, then their 
interaction is limited by their means of communication.
While chatting and VoIP are nice, we believe that it does not quite beat the real thing.
Also, this is a great way for travellers to meet new people when in another city.


Solutie tehnica

Diagrama arhitecturala
Built using Android with local database layer using Realm.
Publisher - Subscriber: publish topics, users subscribe to topics in a city.



FAQ
How do you plan to prevent malicious use of app (violence, crimes, etc)
* The Internet is inherently unsafe. Anyone can pose as anyone even without anonymity.
You can choose fake images and a fake username which create a false sense
of security which can be much more dangerous than straight-up anonymity.

How to counteract spamming?
* We have a hard anti-advertising policy regarding regular topics. Topics containing
commercial content will be deleted in order to avoid possible attempts to spam.
Future features may include keyword filtering of topics and user reporting

Why no categories for topics
* Because the scope of our app is light, spontaneous gatherings. We don't want to 
overcomplicate the design nor divide the community
