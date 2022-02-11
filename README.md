# Telegram Wordle Bot

A telegram bot to play wordle

## Installation

Either use the provided build.sh or build it on your own with maven.

```bash
./build.sh
```

It builds the application and pushes the image to the specified repository.

## Usage

After building and pushing you can deploy it with a docker-compose file
```docker
version: '3'
services:
  wordle-bot:
    restart: always
    container_name: wordle-bot
    image: andiburgr/wordle-bot:1.0.0
    environment:
      - bot_token=[your telegram token comes here]
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)