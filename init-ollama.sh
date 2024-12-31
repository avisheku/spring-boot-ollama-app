#!/bin/bash

# Start Ollama in the background
ollama serve &

# Wait for Ollama to be ready
until curl -s -f "http://localhost:11434/api/version" > /dev/null; do
    echo "Waiting for Ollama to start..."
    sleep 2
done

echo "Ollama is ready. Pulling tinyllama model..."
ollama pull tinyllama

# Keep the container running with the Ollama process
wait $! 