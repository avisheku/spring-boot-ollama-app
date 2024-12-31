#!/bin/bash

# Set Ollama host binding via environment variable
export OLLAMA_HOST=0.0.0.0

# Start Ollama directly (no background)
exec ollama serve 