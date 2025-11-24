package twentyoneh.ru;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {

    private static final List<String> QUICK_FLAGS = Arrays.asList(
            "🛠 Ошибка, невозможно создать заказ",      // У клиента ошибки в МП, из-за которых он не может сам решить вопрос
            "⚠ Ошибка, возможно создать заказ",   // Ошибки в МП, но не мешают рассчитать заказ
            "😠 Негатив/нет времени",   // Негатив, нет времени, категорически не готов сам
            "🚫 Вопрос клиента нельзя решить в МП",    // Вопрос нельзя решить в МП/веб — не хватает опций
            "📦 Нет МП/не знал",        // Нет МП / не знал, что можно в МП
            "🤷 Не разобрался/неудобно",// Не разобрался / было неудобно
            "✖ Скрыть клавиатуру"
    );

    //Нет МП/не знал
    private static final List<String> NO_APP_FLAGS = Arrays.asList(
            "решает сам",
            "не готов решать свой вопрос"
    );

    @Override
    public String getBotUsername() {
        return "HelpPromoteAppBot";
    }

    @Override
    public String getBotToken() {
        return "8503636469:AAE0FdhzX4AXU46Ksdvklp_JHePlotOy5qA";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            try { handleMessage(update.getMessage()); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void handleMessage(Message msg) throws Exception {
        String text = msg.getText();
        String chatId = msg.getChatId().toString();

        if ("/start".equalsIgnoreCase(text)) {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text("Выбери причину — плашкой ниже:")
                    .replyMarkup(buildQuickFlagsKeyboard(2)) // 2 колонки
                    .build());
            System.out.println("Новый пользователь: " + msg.getFrom().getFirstName() + " " + msg.getFrom().getLastName());
            return;
        }
        if(NO_APP_FLAGS.contains(text)) {
            if("не готов решать свой вопрос".equals(text)) {
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("Да, понимаю, иногда бывает сложно разобраться! Позвольте я расскажу вам, как вы можете рассчитать заказ?")
                        .replyMarkup(buildQuickFlagsKeyboard(2))
                        .build());
            }
            if("решает сам".equals(text)) {
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("Выбери причину — плашкой ниже:")
                        .replyMarkup(buildQuickFlagsKeyboard(2)) // 2 колонки
                        .build());
            }
        }

        if (QUICK_FLAGS.contains(text)) {
            if ("✖ Скрыть клавиатуру".equals(text)) {
                ReplyKeyboardRemove hide = new ReplyKeyboardRemove(true);
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("Скрыто. Нажми /start, чтобы вернуть плашки.")
                        .replyMarkup(hide)
                        .build());
                return;
            }

            if("🛠 Ошибка, невозможно создать заказ".equals(text)) {
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("Оформить запрос на 2 линию в ЕОК (Техническая ошибка -> Обращается Физ. лицо)\n" +
                                "Имя, в нашем приложении очень много удобных и полезных опций и жаль, что вы не можете полноценно пользоваться приложением." +
                                " Сейчас я зафиксирую обращение в техническую поддержку. Коллеги свяжутся с вами и помогут")
                        .build());
                return;
            }
            if("😠 Негатив/нет времени".equals(text)) {
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("Имя, хорошо, понимаю, что торопитесь /  что неудобно. Сейчас я помогу вам рассчитать/оформить доставку")
                        .build());
                return;
            }
            if("🚫 Вопрос клиента нельзя решить в МП".equals(text)) {
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("Рассказываем о других возможностях и преимуществах МП, общая информация о доступных селф-сервисах" +
                                "\n\nНЕТ МП: Имя, подскажите, а вы уже скачивали приложение СДЭК? Там можно будет" +
                                "отслеживать движение заказа, и будут приходить уведомления по движению груза -" +
                                "очень удобно. Предлагаю, пока мы на связи, вместе установить приложение. Это займет буквально" +
                                "пару минут. Так вы сразу сможете им воспользоваться, и я буду рядом, чтобы" +
                                "помочь, если возникнут вопросы. Отправляю\n\n"+
                                "ЕСТЬ МП: Имя, кстати, мы обновили приложение СДЭК! Там можно отслеживать " +
                                "движение заказа, и будут приходить уведомления по движению груза - очень удобно.")
                        .build());
                return;
            }
            if("📦 Нет МП/не знал".equals(text)) {
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("Имя, недавно вышло обновление в нашем мобильном приложении, в нем появилось много полезных " +
                                "функций! В приложении гораздо удобнее считать стоимость и срок доставки, ведь в там вы сразу будете видеть варианты тарифов! Имя, мы обновили мобильное\n" +
                                "приложение СДЭК! Попробуете сами или требуется моя помощь?\n\n" +
                                "Рассказать, как можно посчитать/ оформить самостоятельно \n\n" +
                                "Имя, отлично! Отправила вам СМС. Пока у вас скачивается и устанавливается " +
                                "приложение я могу  рассказать вам, как вы сможете там оформить заказ или вы после установки попробуете сами?")
                        .build());
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("Убедиться, что клиент готов решить свой вопрос сам")
                        .replyMarkup(buildNoAppFlagsKeyboard()) // 2 колонки
                        .build());
                return;
            }
            if("🤷 Не разобрался/неудобно".equals(text)) {
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("Имя, расскажите, пожалуйста, какие сложности возникли при " +
                                "использовании приложения Да, понимаю, иногда бывает сложно " +
                                "разобраться! Позвольте я расскажу вам, как вы можете рассчитать заказ? Для этого нажмите на Отправить - далее..." +
                                "Попробуете рассчитать доставку сами или требуется моя помощь?")
                        .build());
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("При наличии предложений/ идей/ пожеланий: оформить запрос на 2 линию в " +
                                "ЕОК (Техническая ошибка Обращается Физ. лицо)\n Имя, спасибо, что поделились!" +
                                "Я передал вашу обратную связь коллегам.")
                        .build());
                return;
            }
            if("⚠ Ошибка, возможно создать заказ".equals(text)) {
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("Имя, в нашем приложении очень много удобных и полезных опций и жаль, что вы не " +
                                "можете в полной мере им пользоваться. Сейчас я зафиксирую обращение в техническую поддержку. Коллеги свяжутся с" +
                                "вами и помогут всё наладить! Кстати, не смотря на эту проблему, у вас все " +
                                "равно есть возможность считать доставку. Это гораздо быстрее, чем обращение в поддержку!\n" +
                                "Давайте я научу вас, как это сделать?")
                        .build());
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("Клиент не хочет:\n"
                        + "Да, понимаю, иногда бывает сложно разобраться! Позвольте я расскажу вам, как вы можете рассчитать" +
                                "заказ? Для этого нажмите на Отправить - далее... Попробуете?")
                        .build());
                return;
            }

            return;
        }


    }

    private ReplyKeyboardMarkup buildNoAppFlagsKeyboard() {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        for (String label : NO_APP_FLAGS) {
            row.add(KeyboardButton.builder().text(label).build());
            if (row.size() >= 2) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        if (!row.isEmpty()) rows.add(row);

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(false)
                .build();
    }

    private ReplyKeyboardMarkup buildQuickFlagsKeyboard(int columns) {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        for (String label : QUICK_FLAGS) {
            row.add(KeyboardButton.builder().text(label).build());
            if (row.size() >= columns) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        if (!row.isEmpty()) rows.add(row);

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(false)
                .build();
    }
}
