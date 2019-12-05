# ktor client usage scaling comparison

A comparison of ktor client usage and how it impacts scalability of ktor server.  This compares creating a singleton client used throughout the lifetime of the server application vs creating and disposing of a new client for each server application call.  It also compares two different client engines, Apache and CIO.  

Each ktor server instance runs in a docker container.  The ktor instance proxies requests to an nginx instance running in a separate container which serves a static file.  Another container runs an instance of [Locust](https://locust.io/) which allows an arbitrary amount of traffic to be directed at the ktor server instance.

Note that an ideal comparison would place each of the above containers on separate machines, but relative performance on a single machine should still be meaningful.


## Run the test

Run one case at a time via docker compose. Shut down all the containers from one case before testing the next:

```
docker-compose -f docker-compose.yml -f docker-compose.call-apache.yml up -d
# Run the test via the Locust UI.
docker-compose -f docker-compose.yml -f docker-compose.call-apache.yml down
```

```
docker-compose -f docker-compose.yml -f docker-compose.call-cio.yml up -d
# Run the test via the Locust UI.
docker-compose -f docker-compose.yml -f docker-compose.call-cio.yml down
```

```
docker-compose -f docker-compose.yml -f docker-compose.singleton-apache.yml up -d
# Run the test via the Locust UI.
docker-compose -f docker-compose.yml -f docker-compose.singleton-apache.yml down
```

```
docker-compose -f docker-compose.yml -f docker-compose.singleton-cio.yml up -d
# Run the test via the Locust UI.
docker-compose -f docker-compose.yml -f docker-compose.singleton-cio.yml down
```

Visit http://localhost:8089 to view the Locust UI.

Enter the number of users to simulate and their spawn rate, then start the test.

Let the test run against the server until numbers are steady.

After the test shut down the docker containers:


## Results

These are the summary results from running the load tests several times each on my local machine.

- Singleton with CIO engine: ~600rps, 100ms median response time
- Singleton with Apache engine: ~500rps, 400ms median response time (with some timeout errors)
- Per Call with CIO engine: ~280rps, 2000ms median response time
- Per Call with Apache engine: ~150rps, 5000ms median response time


The specific numbers are likely to vary depending on the environment, but the overall relationships indicate that:

- A singleton instance of the client scales significantly better than an instance created and disposed per call
- The CIO engine appears to be more performant and less error prone than the Apache engine
