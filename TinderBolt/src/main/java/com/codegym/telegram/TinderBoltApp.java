package com.codegym.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends SimpleTelegramBot {

    public static final String TELEGRAM_BOT_TOKEN = "7503169616:AAHWNSocZUoGnXhzmuQmLVZL--gb1yDQYqA"; //TODO: añadir el token del bot entre comillas
    public static final String OPEN_AI_TOKEN = "gpt:aeVgX6TmWSXXAyKA5EM6JFkblB3TdbhXs54b8ij9z5Vs9eOr"; //TODO: añadir el token de ChatGPT entre comillas

    //Objetos GPT
    private ChatGPTService chatGPT = new ChatGPTService(OPEN_AI_TOKEN); //usar chat gpt
    private DialogMode mode; //dialogar con chatgpt

    private ArrayList<String> lista = new ArrayList<>();//almacenara lista de messages

    public TinderBoltApp() {
        super(TELEGRAM_BOT_TOKEN);
    }

    //TODO: escribiremos la funcionalidad principal del bot aquí


    public void Comandos(){ //modalidad GPT
        mode = DialogMode.MAIN;//porque es el principal
        String text = loadMessage("main");//llama a la plantilla del mensaje
        sendPhotoMessage("main");//llama a la imagen
        sendTextMessage(text);
        //crear un menu
        showMainMenu(
                "start","menú principal del bot",
                "profile","generación de perfil de Tinder \uD83D\uDE0E",
                "opener","mensaje para iniciar conversación \uD83E\uDD70",
                "message","correspondencia en su nombre \uD83D\uDE08",
                "date","correspondencia con celebridades \uD83D\uDD25",
                "gpt","hacer una pregunta a chat GPT \uD83E\uDDE0"
        );
    }

    public void gptComandos(){ //Metodo que permite enviar msj a chatGPT
        mode = DialogMode.GPT;//cambia el modo depende del cliente

        String txt = loadMessage("gpt");//llama al txt gpt de resource para darle instruccion al usuario
        sendPhotoMessage("gpt");
        sendTextMessage(txt);
    }

    public void citasComandos(){ //Metodo que permite enviar msj a chatGPT
        mode = DialogMode.DATE;//cambia el modo depende del cliente

        String txt = loadMessage("date");//llama al txt gpt de resource para darle instruccion al usuario
        sendPhotoMessage("date");
        sendTextMessage(txt);
        sendTextButtonsMessage(txt,"date_ariana","Ariana Grande \uD83D\uDD25",
                "date_margot","Margot Robbie \uD83D\uDD25\uD83D\uDD25",
                "date_zendaya","Zendaya \uD83D\uDD25\uD83D\uDD25\uD83D\uDD25",
                "date_ryan","Ryan Gosling \uD83D\uDE0E",
                "date_tom","Tom Hardy \uD83D\uDE0E\uD83D\uDE0E");
    }
    public void citasBotones(){ //Metodo que permite enviar msj a chatGPT

        String key = getButtonKey();
        sendPhotoMessage(key); // Usa la imagen porque la clave es la misma y llama a la imagen
        sendHtmlMessage(key); // Mensaje completo, es decir, obvia los símbolos reservados
        String prompt = loadPrompt(key); // Pasa valor de la clave ya que cada uno tiene su propio prompt

        chatGPT.setPrompt(prompt);


    }
    public void citasDialogo(){//Metodo que permite enviar msj a chatGPT
        String texto = getMessageText();
        var miMensaje = sendTextMessage("escribiendo...");
        String respuesta = chatGPT.addMessage(texto);// send una nuea sesion cada vez >< add mantiene el historial de chat
        updateTextMessage(miMensaje,respuesta); // mensaje se actualiza cuando llvga la respuesta de chatgpt
    }


    public void mensajeComandos(){
        mode = DialogMode.MESSAGE;
        String txt = loadMessage("message");
        sendPhotoMessage("message");
        sendTextButtonsMessage(txt,"message_next","escribir respuesta",
                "message_date","quiero invitar a esta persona a una cita"

        );
        lista.clear();
    }
    public void mensajeBoton(){
        String key = getButtonKey();
        String prompt = loadPrompt(key); // Pasa valor de la clave ya que cada uno tiene su propio prompt
        String historial = String.join("\n\n", lista);//concatena todo el array para mandarlo a chatgp
        var miMensaje = sendTextMessage("Chat GPT esta ecribiendo...");
        String respuesta = chatGPT.sendMessage(prompt, historial);
        updateTextMessage(miMensaje,respuesta);
    }
    public void mensajeDialogo(){
        String texto = getMessageText();//guarda el mensaje
        lista.add(texto);
    }


    public void comandosPerfil(){
        mode = DialogMode.PROFILE;
        String text = loadMessage("profile");
        sendPhotoMessage("profile");
        sendTextMessage(text);

        sendTextMessage("Como te llamas? ");
        user = new UserInfo();
        contadorPreguntas=0;
    }

    private UserInfo user = new UserInfo();//objeto para los datos del usuario
    private int contadorPreguntas = 0;

    public void dialogoPerfil(){
        String text = getMessageText();
        contadorPreguntas++;//itera el contador en 1 cada vez k entra el metodo

        if (contadorPreguntas ==1){
            user.name = text;
            sendTextMessage("Cual es tu edad? ");
        }else if (contadorPreguntas == 2){
            user.age = text;
            sendTextMessage("Cual es tu hobbie? ");
        } else if (contadorPreguntas == 3) {
            user.hobby=text;
            sendTextMessage("Cual es tu objetivo para interactuar con esta persona");
        } else if (contadorPreguntas == 4) {
            user.goals = text;


            String prompt = loadPrompt("profile");
            String infoUser = user.toString();

            var miMensaje = sendTextMessage("Chat GPT esta ecribiendo...");
            String respuesta = chatGPT.sendMessage(prompt, infoUser);
            updateTextMessage(miMensaje, respuesta);
        }
    }

    public void comandosOpener(){
        mode = DialogMode.OPENER;
        String text = loadMessage("opener");
        sendPhotoMessage("opener");
        sendTextMessage(text);

        sendTextMessage("Como se llama la persona con que quieres hablar? ");
        user = new UserInfo();
        contadorPreguntas=0;
    }

    public void dialogosOpener(){
        String text = getMessageText();
        contadorPreguntas++;//itera el contador en 1 cada vez k entra el metodo

        if (contadorPreguntas ==1){
            user.name = text;
            sendTextMessage("Cual es su edad? ");
        }else if (contadorPreguntas == 2){
            user.age = text;
            sendTextMessage("A que se dedica? ");
        } else if (contadorPreguntas == 3) {
            user.occupation=text;
            sendTextMessage("Cuanto le das del 1 al 10 ? ");
        } else if (contadorPreguntas == 4) {
            user.handsome = text;


            String prompt = loadPrompt("opener");
            String infoUser = user.toString();

            var miMensaje = sendTextMessage("Chat GPT esta ecribiendo...");
            String respuesta = chatGPT.sendMessage(prompt, infoUser);
            updateTextMessage(miMensaje, respuesta);
        }
    }

    public void gptDialogos(){
        String mensaje = getMessageText();
        var miMensaje = sendTextMessage("GPT esta escribiendo...");
        String prompt = loadMessage("gpt");//llama al prompt gpt
        String respuesta = chatGPT.sendMessage(prompt,mensaje);
        updateTextMessage(miMensaje,respuesta);
    }

    public void Inicio() {

        if (mode == DialogMode.GPT) {
            gptDialogos();
        } else if (mode == DialogMode.DATE){
            citasDialogo();
        } else if (mode == DialogMode.MESSAGE) {
            mensajeDialogo();
        }else if(mode == DialogMode.PROFILE){
            dialogoPerfil();
        }else if(mode == DialogMode.OPENER){
            dialogosOpener();
        }else{
            String texto = getMessageText();
            sendTextMessage("*Hola*");
            sendTextMessage("_Como estas?_");
            sendTextMessage("__" + texto);

            sendPhotoMessage("avatar_main");
            sendTextButtonsMessage("Launch procees",
                    "Start","Inicio",
                    "Stop","Parar");//nombre texto,id,palabra
        }

    }

    public void Botones(){

        String key = getButtonKey();
        if(key.equals("Start")){
            sendTextMessage("_Proceso Iniciado_");
        }else {
            sendTextMessage("Proceso Finalizado*");
        }
    }



    @Override
    public void onInitialize() {
        //TODO: y un poco más aquí :)
        addCommandHandler("start", this::Comandos);// primero va el texto
        addCommandHandler("gpt", this::gptComandos);// metodo para isar chatgpt
        addCommandHandler("date", this::citasComandos);
        addCommandHandler("message", this::mensajeComandos);
        addCommandHandler("profile", this::comandosPerfil);
        addCommandHandler("opener", this::comandosOpener);
        addMessageHandler(this::Inicio);//metodo para mandar mensajes
        //addButtonHandler("^.*",this::Botones);//para botones (^-> inicio . -> cualquier cosa *-> ciualquier cantidad)
        addButtonHandler("^date_.*",this::citasBotones);
        addButtonHandler("^message_.*",this::mensajeBoton);


    }


    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
