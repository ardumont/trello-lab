run-dev:
	lein ring server

testing:
	lein expectations

read:
	curl http://localhost:3000/metadata/ && echo "\n"

record:
	curl -v -H'Content-Type: application/json' -XPUT -d '{"mode": "record", "server-uri": "http://api.trello.com/1"}' http://localhost:3000/metadata/

replay:
	curl -v -H'Content-Type: application/json' -XPUT -d '{"mode": "replay", "server-uri": "http://api.trello.com/1"}' http://localhost:3000/metadata/

save:
	curl -v -XPUT http://localhost:3000/metadata/save/

load:
	curl -v http://localhost:3000/metadata/load/
