curl -i \
  -H "Content-Type:application/json" \
  -d "@create-person-command.json" \
  "http://localhost:8081/persons/create" 
