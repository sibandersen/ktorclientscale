from locust import HttpLocust, TaskSet, task, between

class ClientBehavior(TaskSet):

    @task(1)
    def index(self):
        self.client.get("/")

class ClientLocust(HttpLocust):
    task_set = ClientBehavior
    wait_time = between(1, 2)
