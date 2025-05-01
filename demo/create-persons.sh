for file in ./create-*-command.json
do
  curl -i \
    -H "Content-Type:application/json" \
    -d "@$file" \
    "http://localhost:8081/persons/create" 
done
