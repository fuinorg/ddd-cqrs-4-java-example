for file in ./*.json
do
  curl -i \
    -H "Content-Type:application/json" \
    -d "@$file" \
    "http://localhost:8081/persons/create" 
done
