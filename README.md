# 🤖 Spring Boot Telegram LLM Proxy Bot

A simple yet powerful Telegram bot built with Spring Boot that acts as a proxy to Large Language Models (LLMs) via the [OpenRouter API](https://openrouter.ai/). Users can choose a model using inline buttons directly in Telegram.

---

## 🚀 Features

- ✅ Supports OpenRouter API for LLM access
- ✅ Inline buttons for switching models (`/models`)
- ✅ Per-user model selection
- ✅ Built with Spring Boot 3.x and Java 17+
- ✅ Uses `TelegramLongPollingBot` (stable alternative to webhook)

---

## 🧰 Tech Stack

- Java 17+
- Spring Boot
- [TelegramBots](https://github.com/rubenlagus/TelegramBots) via JitPack
- OpenRouter.ai API (can be replaced with OpenAI, Mistral, Together.ai, etc.)

---

## ⚙️ Setup and Run

### 1. Clone the repository

```bash
git clone https://github.com/your-username/spring-telegram-llm-bot.git
cd spring-telegram-llm-bot
```

### 2. Configure application.yml

Edit src/main/resources/application.yml:
telegram:bot:token:YOUR_TELEGRAM_BOT_TOKEN
telegram:bot:username:YOUR_BOT_USERNAME
open-router:api-key:YOUR_API_KEY

### 3. Run the bot

```bash
./gradlew bootRun
```
