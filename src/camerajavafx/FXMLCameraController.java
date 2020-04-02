/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package camerajavafx;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import java.awt.Canvas;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author jean
 */
public class FXMLCameraController implements Initializable {
    
    @FXML
    private Button buttonReiniciar;

    @FXML
    private Button buttonTirarFoto;

    @FXML
    private Button buttonSalvar;

    @FXML
    private Button buttonCancelar;
    
    @FXML 
    private ImageView imgWebCamCapturedImage;
    
    @FXML 
    private FlowPane fpBottomPane;
    
    private BufferedImage foto;
    private Webcam webCam = null;
    private boolean stopCamera = false;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
    Image mainiamge;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        fpBottomPane.setDisable(true);

        try{
            initializeWebCam(0);
        }catch(Exception e){
            e.printStackTrace();
        }

    }    

    @FXML
    private void tirarFoto(ActionEvent event) throws IOException {
        stopCamera = true;
        buttonReiniciar.setDisable(false);
        buttonTirarFoto.setDisable(true);
        buttonSalvar.setDisable(false);
      
    }

    protected void initializeWebCam(final int webCamIndex) {

        Task<Void> webCamIntilizer = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                if(webCam == null)
                {
                    webCam = Webcam.getWebcams().get(webCamIndex);
                    webCam.open();
                }else
                {
                    closeCamera();
                    webCam = Webcam.getWebcams().get(webCamIndex);
                    webCam.open();

                }
                startWebCamStream();
                return null;
            }

        };

        new Thread(webCamIntilizer).start();
        fpBottomPane.setDisable(false);
        buttonReiniciar.setDisable(true);
        buttonSalvar.setDisable(true);

    }
    
    private void startWebCamStream() {

        stopCamera  = false;
        Task<Void> task = new Task<Void>() {


            @Override
            protected Void call() throws Exception {

                while (!stopCamera) {
                    try {
                        if ((foto = webCam.getImage()) != null) {

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                        mainiamge = SwingFXUtils
                                            .toFXImage(foto, null);
                                    imageProperty.set(mainiamge);
                                }
                            });

                           foto.flush();

                        }
                    } catch (Exception e) {
                    } finally {

                    }

                }

                return null;

            }

        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        imgWebCamCapturedImage.imageProperty().bind(imageProperty);
  

    }
    
     private void closeStage() {
         closeCamera();
        ((Stage) fpBottomPane.getScene().getWindow()).close();
    }

    private void closeCamera()
    {
        if(webCam != null)
        {
            webCam.close();
        }
    }
    
    @FXML
    private void salvar() throws IOException{
        ImageIO.write(foto, "PNG", new File("test.png"));
        closeStage();
    }

   
    @FXML
    private void reiniciar(ActionEvent event)
    {
        stopCamera = false;
        startWebCamStream();
        buttonReiniciar.setDisable(true);
        buttonTirarFoto.setDisable(false);
        buttonSalvar.setDisable(false);
    }
    
    
    @FXML
    private void cancelar(ActionEvent event)
    {
        stopCamera  = true;
        closeStage();
    }
    
    
    
}
