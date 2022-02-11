package at.burgr.wordlebot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;

@SpringBootApplication
public class WordleBot implements CommandLineRunner {

	Logger logger = LoggerFactory.getLogger(WordleBot.class);

	@Autowired
	private Environment environment;

	@Autowired
	private Dictionary dictionary;

	public static void main(String[] args) {
		SpringApplication.run(WordleBot.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		final int maxAttempts = environment.getProperty("max_attempts", Integer.class);
		final int wordLength = environment.getProperty("word_length", Integer.class);
		
		TelegramBot bot = new TelegramBot(environment.getProperty("bot_token"));
		Map<Long, GameProgress> gameProgresses = new HashMap<>();

		bot.setUpdatesListener(updates -> {
			updates.forEach((update) -> {
				try {
					logger.info(update.toString());
					Long chatId = update.message().chat().id();
					String attempt = update.message().text();

					if (attempt.charAt(0) == '/') {
						// control message
						return;
					}

					GameProgress progress = gameProgresses.get(chatId);
					if (progress == null) {
						try {
							gameProgresses.put(chatId, new GameProgress(dictionary.getNewWord(), maxAttempts));
						} catch (IOException e) {
							logger.error("Error while reading wordlist", e);
						}
						progress = gameProgresses.get(chatId);
						logger.info("New word: ChatId: " + chatId + " Word: " + gameProgresses.get(chatId).getWord());
						bot.execute(new SendMessage(chatId, "Starte ein neues Spiel.\n\nSpielregeln:\n"
								+ "🔸 " + wordLength + " Zeichen\n"
								+ "🔸 " + maxAttempts + " Versuche\n"
								+ "🔸 " + "Umlaute: ä = ae"));
					}

					String match = progress.match(dictionary.getAllWords(), gameProgresses.get(chatId).getWord(),
							attempt);

					// finished or not?
					if (progress.isSuccess() || progress.getAttempts() >= Long.valueOf(maxAttempts)) {
						StringBuilder summary = new StringBuilder();
						summary.append(
								String.format("Wordle %d/%d\n", progress.getAttempts(), progress.getMaxAttempts()));
						summary.append(progress.getHistory()).append("\n");

						if (progress.isSuccess()) {
							summary.append("Gratuliere! 🥳");
						} else {
							summary.append(progress.getWord()).append("\n");
							summary.append("Kopf hoch - das passiert den Besten! 🥴");
						}

						bot.execute(new SendMessage(chatId, summary.toString()));
						gameProgresses.remove(chatId);
					} else {
						bot.execute(new SendMessage(chatId, match));
					}
				} catch (Exception e) {
					logger.error("Error while processing update", e);
				}
			});

			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		});
	}
}
