package org.polytechtours.javaperformance.tp.paintingants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.Timer;

/**
 * The PaintingAnts class represents an applet that simulates ants painting on
 * an image.
 * It extends the java.applet.Applet class and implements the Runnable
 * interface.
 * The applet allows customization of various parameters such as threshold
 * luminance,
 * number of ants, and parameters for each ant including colors, position,
 * direction, and size.
 * The ants move on the image and leave trails of colors as they move.
 */
public class PaintingAnts extends java.applet.Applet implements Runnable {
  private static final long serialVersionUID = 1L;
  // parametres
  private int mLargeur;
  private int mHauteur;

  // l'objet graphique lui meme
  private CPainting mPainting;

  // les fourmis
  private LinkedList<CFourmi> mColonie = new LinkedList<CFourmi>();
  private CColonie mColony;

  // random
  private Random rand = new Random();

  private Thread mApplis, mThreadColony;

  private Dimension mDimension;
  private long mCompteur = 0;
  private Object mMutexCompteur = new Object();
  private boolean mPause = false;

  public BufferedImage mBaseImage;
  private Timer fpsTimer;

  /** Fourmis per second :) */
  private Long fpsCounter = 0L;
  /** stocke la valeur du compteur lors du dernier timer */
  private Long lastFps = 0L;

  /**
   * Increments the counter in a thread-safe manner.
   */
  public void compteur() {
    synchronized (mMutexCompteur) {
      mCompteur++;
    }
  }

  /**
   * Destroys the object and releases any resources held by it.
   * This method is called when the object is no longer needed and should be
   * cleaned up.
   */
  @Override
  public void destroy() {
    // System.out.println(this.getName()+ ":destroy()");

    if (mApplis != null) {
      mApplis = null;
    }
  }

  /**
   * Returns the information about the applet.
   *
   * @return the information about the applet as a String
   */
  @Override
  public String getAppletInfo() {
    return "Painting Ants";
  }

  /**
   * Returns the parameter information for the PaintingAnts class.
   * 
   * @return a 2D array containing the parameter information. Each row represents
   *         a parameter and contains the following information:
   *         - Parameter name
   *         - Parameter type
   *         - Parameter description
   */
  @Override
  public String[][] getParameterInfo() {
    String[][] lInfo = { { "SeuilLuminance", "string", "Seuil de luminance" }, { "Img", "string", "Image" },
        { "NbFourmis", "string", "Nombre de fourmis" }, { "Fourmis", "string",
            "Paramètres des fourmis (RGB_déposée)(RGB_suivie)(x,y,direction,taille)(TypeDeplacement,ProbaG,ProbaTD,ProbaD,ProbaSuivre);...;" } };
    return lInfo;
  }

  /**
   * Returns the current pause state of the PaintingAnts object.
   *
   * @return true if the PaintingAnts object is paused, false otherwise.
   */
  public boolean getPause() {
    return mPause;
  }

  /**
   * Increments the FPS (Frames Per Second) counter.
   */
  public synchronized void IncrementFpsCounter() {
    fpsCounter++;
  }

  /**
   * Initializes the applet.
   */
  @Override
  public void init() {
    URL lFileName;
    URLClassLoader urlLoader = (URLClassLoader) this.getClass().getClassLoader();

    // lecture des parametres de l'applet

    mDimension = getSize();
    mLargeur = mDimension.width;
    mHauteur = mDimension.height;

    mPainting = new CPainting(mDimension, this);
    add(mPainting);

    // lecture de l'image
    lFileName = urlLoader.findResource("images/" + getParameter("Img"));
    try {
      if (lFileName != null) {
        mBaseImage = javax.imageio.ImageIO.read(lFileName);
      }
    } catch (java.io.IOException ex) {
    }

    if (mBaseImage != null) {
      mLargeur = mBaseImage.getWidth();
      mHauteur = mBaseImage.getHeight();
      mDimension.setSize(mLargeur, mHauteur);
      resize(mDimension);
    }

    readParameterFourmis();

    setLayout(null);
  }

  /**
   * This method is responsible for painting the image on the graphics object.
   * It checks if the base image is null and if not, it draws the image on the
   * graphics object.
   *
   * @param g The graphics object on which the image is to be painted.
   */
  @Override
  public void paint(Graphics g) {

    if (mBaseImage == null) {
      return;
    }
    g.drawImage(mBaseImage, 0, 0, this);
  }

  /**
   * Toggles the pause state of the painting ants simulation.
   * If the simulation is currently paused, it will be resumed.
   * If the simulation is currently running, it will be paused.
   */
  public void pause() {
    mPause = !mPause;
    // if (!mPause)
    // {
    // notify();
    // }
  }

  /**
   * Reads a float parameter from a string.
   * 
   * @param pStr the string containing the float parameter
   * @return the float value read from the string
   */
  private float readFloatParameter(String pStr) {
    float lMin, lMax, lResult;
    // System.out.println(" chaine pStr: "+pStr);
    StringTokenizer lStrTok = new StringTokenizer(pStr, ":");
    // on lit une premiere valeur
    lMin = Float.parseFloat(lStrTok.nextToken());
    // System.out.println(" lMin: "+lMin);
    lResult = lMin;
    // on essaye d'en lire une deuxieme
    try {
      lMax = Float.parseFloat(lStrTok.nextToken());
      // System.out.println(" lMax: "+lMax);
      if (lMax > lMin) {
        // on choisit un nombre entre lMin et lMax
        lResult = (float) (Math.random() * (lMax - lMin)) + lMin;
      }
    } catch (java.util.NoSuchElementException e) {
      // il n'y pas de deuxieme nombre et donc le nombre retourné correspond au
      // premier nombre
    }
    return lResult;
  }

  /**
   * Reads an integer parameter from a string and returns the result.
   * The string should be in the format "min:max", where min and max are integer
   * values.
   * If a max value is provided and it is greater than the min value, a random
   * number between min and max (inclusive) is returned.
   * If no max value is provided, the min value is returned.
   *
   * @param pStr the string containing the integer parameter in the format
   *             "min:max"
   * @return the integer value read from the string
   */
  private int readIntParameter(String pStr) {
    int lMin, lMax, lResult;
    StringTokenizer lStrTok = new StringTokenizer(pStr, ":");
    // on lit une premiere valeur
    lMin = Integer.parseInt(lStrTok.nextToken());
    lResult = lMin;
    // on essaye d'en lire une deuxieme
    try {
      lMax = Integer.parseInt(lStrTok.nextToken());
      if (lMax > lMin) {
        // on choisit un nombre entre lMin et lMax
        lResult = (int) (rand.nextDouble() * (lMax - lMin + 1)) + lMin;
      }
    } catch (java.util.NoSuchElementException e) {
      // il n'y pas de deuxieme nombre et donc le nombre retourné correspond au
      // premier nombre
    }
    return lResult;
  }

  /**
   * Reads the parameters for the ants.
   * This method reads the threshold luminance and the number of ants from the
   * HTML parameters.
   * If the parameters are not defined, default values are used.
   * It also reads the parameters for each ant, including the deposited color,
   * followed color, initial position, direction, and size.
   * If the parameters are not defined, random values are used.
   * Finally, it creates and adds the ants to the colony.
   */
  private void readParameterFourmis() {
    String lChaine;
    int R, G, B;
    Color lCouleurDeposee, lCouleurSuivie;
    CFourmi lFourmi;
    float lProbaTD, lProbaG, lProbaD, lProbaSuivre, lSeuilLuminance;
    char lTypeDeplacement = ' ';
    int lInitDirection, lTaille;
    float lInit_x, lInit_y;
    int lNbFourmis = -1;

    // Lecture des paramètres des fourmis

    // Lecture du seuil de luminance
    // <PARAM NAME="SeuilLuminance" VALUE="N">
    // N : seuil de luminance : -1 = random(2..60), x..y = random(x..y)
    lChaine = getParameter("SeuilLuminance");
    if (lChaine != null) {
      lSeuilLuminance = readFloatParameter(lChaine);
    } else {
      // si seuil de luminance n'est pas défini
      lSeuilLuminance = 40f;
    }
    System.out.println("Seuil de luminance:" + lSeuilLuminance);

    // Lecture du nombre de fourmis :
    // <PARAM NAME="NbFourmis" VALUE="N">
    // N : nombre de fourmis : -1 = random(2..6), x..y = random(x..y)
    lChaine = getParameter("NbFourmis");
    if (lChaine != null) {
      lNbFourmis = readIntParameter(lChaine);
    } else {
      // si le parametre NbFourmis n'est pas défini
      lNbFourmis = -1;
    }
    // si le parametre NbFourmis n'est pas défini ou alors s'il vaut -1 :
    if (lNbFourmis == -1) {
      // Le nombre de fourmis est aléatoire entre 2 et 6 !
      lNbFourmis = (int) (rand.nextDouble() * 5) + 2;
    }

    // <PARAM NAME="Fourmis"
    // VALUE="(255,0,0)(255,255,255)(20,40,1)([d|o],0.2,0.6,0.2,0.8)">
    // (R,G,B) de la couleur déposée : -1 = random(0...255); x:y = random(x...y)
    // (R,G,B) de la couleur suivie : -1 = random(0...255); x:y = random(x...y)
    // (x,y,d,t) position , direction initiale et taille du trait
    // x,y = 0.0 ... 1.0 : -1 = random(0.0 ... 1.0); x:y = random(x...y)
    // d = 7 0 1
    // 6 X 2
    // 5 4 3 : -1 = random(0...7); x:y = random(x...y)
    // t = 0, 1, 2, 3 : -1 = random(0...3); x:y = random(x...y)
    //
    // (type deplacement,proba gauche,proba tout droit,proba droite,proba
    // suivre)
    // type deplacement = o/d : -1 = random(o/d)
    // probas : -1 = random(0.0 ... 1.0); x:y = random(x...y)

    lChaine = getParameter("Fourmis");
    if (lChaine != null) {
      // on affiche la chaine de parametres
      System.out.println("Paramètres:" + lChaine);

      // on va compter le nombre de fourmis dans la chaine de parametres :
      lNbFourmis = 0;
      // chaine de paramètres pour une fourmi
      StringTokenizer lSTFourmi = new StringTokenizer(lChaine, ";");
      while (lSTFourmi.hasMoreTokens()) {
        // chaine de parametres de couleur et proba
        StringTokenizer lSTParam = new StringTokenizer(lSTFourmi.nextToken(), "()");
        // lecture de la couleur déposée
        StringTokenizer lSTCouleurDéposée = new StringTokenizer(lSTParam.nextToken(), ",");
        R = readIntParameter(lSTCouleurDéposée.nextToken());
        if (R == -1) {
          R = rand.nextInt(256);
        }

        G = readIntParameter(lSTCouleurDéposée.nextToken());
        if (G == -1) {
          G = rand.nextInt(256);
        }
        B = readIntParameter(lSTCouleurDéposée.nextToken());
        if (B == -1) {
          B = rand.nextInt(256);
        }
        lCouleurDeposee = new Color(R, G, B);
        System.out.print("Parametres de la fourmi " + lNbFourmis + ":(" + R + "," + G + "," + B + ")");

        // lecture de la couleur suivie
        StringTokenizer lSTCouleurSuivi = new StringTokenizer(lSTParam.nextToken(), ",");
        R = readIntParameter(lSTCouleurSuivi.nextToken());
        G = readIntParameter(lSTCouleurSuivi.nextToken());
        B = readIntParameter(lSTCouleurSuivi.nextToken());
        lCouleurSuivie = new Color(R, G, B);
        System.out.print("(" + R + "," + G + "," + B + ")");

        // lecture de la position de la direction de départ et de la taille de
        // la trace
        StringTokenizer lSTDéplacement = new StringTokenizer(lSTParam.nextToken(), ",");
        lInit_x = readFloatParameter(lSTDéplacement.nextToken());
        if (lInit_x < 0.0 || lInit_x > 1.0) {
          lInit_x = (float) Math.random();
        }
        lInit_y = readFloatParameter(lSTDéplacement.nextToken());
        if (lInit_y < 0.0 || lInit_y > 1.0) {
          lInit_y = (float) Math.random();
        }
        lInitDirection = readIntParameter(lSTDéplacement.nextToken());
        if (lInitDirection < 0 || lInitDirection > 7) {
          lInitDirection = rand.nextInt(8);
        }
        lTaille = readIntParameter(lSTDéplacement.nextToken());
        if (lTaille < 0 || lTaille > 3) {
          lTaille = rand.nextInt(4);
        }
        System.out.print("(" + lInit_x + "," + lInit_y + "," + lInitDirection + "," + lTaille + ")");

        // lecture des probas
        StringTokenizer lSTProbas = new StringTokenizer(lSTParam.nextToken(), ",");
        lTypeDeplacement = lSTProbas.nextToken().charAt(0);
        // System.out.println(" lTypeDeplacement:"+lTypeDeplacement);

        if (lTypeDeplacement != 'o' && lTypeDeplacement != 'd') {
          if (Math.random() < 0.5) {
            lTypeDeplacement = 'o';
          } else {
            lTypeDeplacement = 'd';
          }
        }

        lProbaG = readFloatParameter(lSTProbas.nextToken());
        lProbaTD = readFloatParameter(lSTProbas.nextToken());
        lProbaD = readFloatParameter(lSTProbas.nextToken());
        lProbaSuivre = readFloatParameter(lSTProbas.nextToken());
        // on normalise au cas ou
        float lSomme = lProbaG + lProbaTD + lProbaD;
        lProbaG /= lSomme;
        lProbaTD /= lSomme;
        lProbaD /= lSomme;

        System.out.println(
            "(" + lTypeDeplacement + "," + lProbaG + "," + lProbaTD + "," + lProbaD + "," + lProbaSuivre + ");");

        // création de la fourmi
        lFourmi = new CFourmi(lCouleurDeposee, lCouleurSuivie, lProbaTD, lProbaG, lProbaD, lProbaSuivre, mPainting,
            lTypeDeplacement, lInit_x, lInit_y, lInitDirection, lTaille, lSeuilLuminance, this);
        mColonie.add(lFourmi);
        lNbFourmis++;
      }
    } else // initialisation aléatoire des fourmis
    {

      int i;
      Color lTabColor[] = new Color[lNbFourmis];
      int lColor;

      // initialisation aléatoire de la couleur de chaque fourmi
      for (i = 0; i < lNbFourmis; i++) {
        R = rand.nextInt(256);
        G = rand.nextInt(256);
        B = rand.nextInt(256);
        lTabColor[i] = new Color(R, G, B);
      }

      // construction des fourmis
      for (i = 0; i < lNbFourmis; i++) {
        // la couleur suivie est la couleur d'une autre fourmi
        lColor = rand.nextInt(lNbFourmis);
        if (i == lColor) {
          lColor = (lColor + 1) % lNbFourmis;
        }

        // une chance sur deux d'avoir un déplacement perpendiculaire
        if ((float) Math.random() < 0.5f) {
          lTypeDeplacement = 'd';
        } else {
          lTypeDeplacement = 'o';
        }

        // position initiale
        lInit_x = (float) (Math.random()); // *mPainting.getLargeur()
        lInit_y = (float) (Math.random()); // *mPainting.getHauteur()

        // direction initiale
        lInitDirection = rand.nextInt(8);

        // taille du trait
        lTaille = rand.nextInt(4);

        // proba de déplacement :
        lProbaTD = (float) (Math.random());
        lProbaG = (float) (Math.random() * (1.0 - lProbaTD));
        lProbaD = (float) (1.0 - (lProbaTD + lProbaG));
        lProbaSuivre = (float) (0.5 + 0.5 * Math.random());

        System.out.print(
            "Random:(" + lTabColor[i].getRed() + "," + lTabColor[i].getGreen() + "," + lTabColor[i].getBlue() + ")");
        System.out.print("(" + lTabColor[lColor].getRed() + "," + lTabColor[lColor].getGreen() + ","
            + lTabColor[lColor].getBlue() + ")");
        System.out.print("(" + lInit_x + "," + lInit_y + "," + lInitDirection + "," + lTaille + ")");
        System.out.println(
            "(" + lTypeDeplacement + "," + lProbaG + "," + lProbaTD + "," + lProbaD + "," + lProbaSuivre + ");");

        // création et ajout de la fourmi dans la colonie
        lFourmi = new CFourmi(lTabColor[i], lTabColor[lColor], lProbaTD, lProbaG, lProbaD, lProbaSuivre, mPainting,
            lTypeDeplacement, lInit_x, lInit_y, lInitDirection, lTaille, lSeuilLuminance, this);
        mColonie.add(lFourmi);
      }
    }
    // on affiche le nombre de fourmis
    // System.out.println("Nombre de Fourmis:"+lNbFourmis);
  }

  /**
   * Executes the main logic of the program.
   * This method is called when the thread starts.
   * It initializes the painting, starts the colony thread, and continuously
   * updates the status message.
   * If the program is paused, it displays "pause" as the status message.
   * If the program is running, it displays the current frames per second (FPS)
   * and a progress bar based on the counter value.
   * The method sleeps for 10 milliseconds between updates.
   */
  @Override
  public void run() {
    // System.out.println(this.getName()+ ":run()");

    int i;
    StringBuilder lMessage = new StringBuilder();

    mPainting.init();

    Thread currentThread = Thread.currentThread();

    /*
     * for ( i=0 ; i<mColonie.size() ; i++ ) {
     * ((CFourmi)mColonie.elementAt(i)).start(); }
     */

    mThreadColony.start();

    while (mApplis == currentThread) {
      lMessage.setLength(0);
      if (mPause) {
        lMessage.append("pause");
      } else {
        synchronized (this) {
          lMessage.append("running (" + lastFps + ") ");
        }

        synchronized (mMutexCompteur) {
          mCompteur %= 10000;
          for (i = 0; i < mCompteur / 1000; i++) {
            lMessage.append(".");
          }
        }

      }
      showStatus(lMessage.toString());

      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        // showStatus(e.toString());
      }
    }
  }

  /**
   * Starts the painting ants application.
   * Initializes the colony, creates and starts the colony thread,
   * sets up a timer to update the frames per second (FPS),
   * and starts the application thread.
   */
  @Override
  public void start() {
    // System.out.println(this.getName()+ ":start()");
    mColony = new CColonie(mColonie, this);
    mThreadColony = new Thread(mColony);
    mThreadColony.setPriority(Thread.MIN_PRIORITY);

    fpsTimer = new Timer(1000, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateFPS();
      }
    });
    fpsTimer.setRepeats(true);
    fpsTimer.start();

    // showStatus("starting...");
    // Create the thread.
    mApplis = new Thread(this);
    // and let it start running
    mApplis.setPriority(Thread.MIN_PRIORITY);
    mApplis.start();
  }

  /**
   * Stops the painting ants simulation.
   * This method stops the FPS timer, requests the colony thread to stop,
   * and waits for the colony thread to finish. It also resets the thread and
   * application references.
   */
  @Override
  public void stop() {
    // showStatus("stopped...");

    fpsTimer.stop();

    // On demande au Thread Colony de s'arreter et on attend qu'il s'arrete
    mColony.pleaseStop();
    try {
      mThreadColony.join();
    } catch (Exception e) {
    }

    mThreadColony = null;
    mApplis = null;
  }

  /**
   * Updates the frames per second (FPS) counter.
   * This method is synchronized to ensure thread safety.
   */
  private synchronized void updateFPS() {
    lastFps = fpsCounter;
    fpsCounter = 0L;
  }
}
