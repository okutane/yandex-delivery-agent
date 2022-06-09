
## Prerequisites ##
You need to have `docker` and `docker-compose` installed on your machine.

## Usage ##
Running agent

```CALLBACK_URL="https://mycompany.com/path/hook" ACCESS_TOKEN=my_token docker-compose up```

Removing app container and image before rebuild. (Needed if code is changed)
```
$ docker-compose rm
Going to remove app, db
Are you sure? [yN] y
Removing app ... done
Removing db  ... done
$ docker image rm agent_app
Untagged: agent_app:latest
Deleted: sha256:435c018126a8ccb5681552e7568c91c21c45d6a969894e4949d0b410373e7c0e
Deleted: sha256:8df13713b679e6bdebbb7c5d799cf180876d814b0a053664a7786d220a44dea4
Deleted: sha256:d19451f9bc5890881b77cf31a3e356fa6899bac742da412b442f8e9098b343f3
Deleted: sha256:232a08af0d7fb266efc95730c961b4a4bad01410435d7098f5b7544df2618015
Deleted: sha256:92a8b906452d68ade630247d1adaf3290ef16bdca076ca1ea79db340db54e299
```

## Common errors ##
```
app    | Caused by: feign.FeignException$Unauthorized: [401 Unauthorized] during [POST] to [https://b2b.taxi.yandex.net/b2b/cargo/integration/v1/claims/journal] [B2bClient#readJournal(JournalRequest)]: [{"code":"unauthorized","message":"Access denied"}]
```
Reason: `ACCESS_TOKEN` parameter may be invalid.

```
app    | 2022-06-09 16:33:48.263 ERROR 1 --- [           main] r.y.delivery.agent.AgentApplication      : Hook notification error
app    | 
app    | feign.FeignException$NotFound: [404 Not Found] during [POST] to [https://mycompany.com/path/hook?claim_id=fffffffffffffffffffffffffffff&change_type=status_changed&updated_ts=2022-06-08T10%3A25%3A02.025688%2B00%3A00] [CallbackClient#notify(Map)]: [{ "success": false }]
```
Reason: problem with callback handler, check your company service. The agent will automatically try again in a while.
