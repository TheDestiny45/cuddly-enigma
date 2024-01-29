package org.polytechtours.javaperformance.tp.paintingants;
// package PaintingAnts_v2;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;

// version : 2.0

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * <p>
 * Titre : Painting Ants
 * </p>
 * <p>
 * Description :
 * </p>
 * <p>
 * Copyright : Copyright (c) 2003
 * </p>
 * <p>
 * Société : Equipe Réseaux/TIC - Laboratoire d'Informatique de l'Université de
 * Tours
 * </p>
 *
 * @author Nicolas Monmarché
 * @version 1.0
 */

/**
 * Represents a painting area for the PaintingAnts application.
 * This class extends the Canvas class and implements the MouseListener
 * interface.
 * It provides methods for initializing the painting area, setting the
 * background color,
 * retrieving the color at a specific coordinate, getting the dimension of the
 * painting,
 * getting the height and width of the painting, initializing the painting,
 * handling mouse click events,
 * and painting the graphics on the screen.
 */
public class CPainting extends Canvas implements MouseListener {
  private static final long serialVersionUID = 1L;
  // matrice servant pour le produit de convolution
  private static float[][] mMatriceConv9 = new float[3][3];
  private static float[][] mMatriceConv25 = new float[5][5];
  private static float[][] mMatriceConv49 = new float[7][7];
  // Objet de type Graphics permettant de manipuler l'affichage du Canvas
  private Graphics mGraphics;
  // Objet ne servant que pour les bloc synchronized pour la manipulation du
  // tableau des couleurs
  private Object mMutexCouleurs = new Object();
  // tableau des couleurs, il permert de conserver en memoire l'état de chaque
  // pixel du canvas, ce qui est necessaire au deplacemet des fourmi
  // il sert aussi pour la fonction paint du Canvas
  private Color[][] mCouleurs;
  // couleur du fond
  private Color mCouleurFond = new Color(255, 255, 255);
  // dimensions
  private Dimension mDimension = new Dimension();

  private PaintingAnts mApplis;

  private boolean mSuspendu = false;

  /******************************************************************************
   * Titre : public CPainting() Description : Constructeur de la classe
   ******************************************************************************/
  /**
   * Represents a painting area for the PaintingAnts application.
   * This class extends the JPanel class and provides methods for initializing the
   * painting area,
   * setting the background color, and initializing the color matrix.
   */
  public CPainting(Dimension pDimension, PaintingAnts pApplis) {
    int i, j;
    addMouseListener(this);

    mApplis = pApplis;

    mDimension = pDimension;
    setBounds(new Rectangle(0, 0, mDimension.width, mDimension.height));

    this.setBackground(mCouleurFond);

    // initialisation de la matrice des couleurs
    mCouleurs = new Color[mDimension.width][mDimension.height];
    synchronized (mMutexCouleurs) {
      for (i = 0; i != mDimension.width; i++) {
        for (j = 0; j != mDimension.height; j++) {
          mCouleurs[i][j] = new Color(mCouleurFond.getRed(), mCouleurFond.getGreen(), mCouleurFond.getBlue());
        }
      }
    }
  }

  /**
   * Retrieves the color at the specified coordinates.
   *
   * @param x The x-coordinate of the pixel.
   * @param y The y-coordinate of the pixel.
   * @return The color at the specified coordinates.
   */
  public Color getCouleur(int x, int y) {
    synchronized (mMutexCouleurs) {
      return mCouleurs[x][y];
    }
  }

  /**
   * Returns the dimension of the painting.
   *
   * @return the dimension of the painting
   */
  public Dimension getDimension() {
    return mDimension;
  }

  /**
   * Returns the height of the painting.
   *
   * @return the height of the painting
   */
  public int getHauteur() {
    return mDimension.height;
  }

  /**
   * Returns the width of the painting.
   *
   * @return the width of the painting
   */
  public int getLargeur() {
    return mDimension.width;
  }

  /**
   * Initializes the painting.
   */
  public void init() {
    int i, j;
    mGraphics = getGraphics();
    synchronized (mMutexCouleurs) {
      mGraphics.clearRect(0, 0, mDimension.width, mDimension.height);

      // initialisation de la matrice des couleurs

      for (i = 0; i != mDimension.width; i++) {
        for (j = 0; j != mDimension.height; j++) {
          mCouleurs[i][j] = new Color(mCouleurFond.getRed(), mCouleurFond.getGreen(), mCouleurFond.getBlue());
        }
      }
    }

    // initialisation de la matrice de convolution : lissage moyen sur 9
    // cases
    /*
     * 1 2 1 2 4 2 1 2 1
     */
    CPainting.mMatriceConv9[0][0] = 1 / 16f;
    CPainting.mMatriceConv9[0][1] = 2 / 16f;
    CPainting.mMatriceConv9[0][2] = 1 / 16f;
    CPainting.mMatriceConv9[1][0] = 2 / 16f;
    CPainting.mMatriceConv9[1][1] = 4 / 16f;
    CPainting.mMatriceConv9[1][2] = 2 / 16f;
    CPainting.mMatriceConv9[2][0] = 1 / 16f;
    CPainting.mMatriceConv9[2][1] = 2 / 16f;
    CPainting.mMatriceConv9[2][2] = 1 / 16f;

    // initialisation de la matrice de convolution : lissage moyen sur 25
    // cases
    /*
     * 1 1 2 1 1 1 2 3 2 1 2 3 4 3 2 1 2 3 2 1 1 1 2 1 1
     */
    CPainting.mMatriceConv25[0][0] = 1 / 44f;
    CPainting.mMatriceConv25[0][1] = 1 / 44f;
    CPainting.mMatriceConv25[0][2] = 2 / 44f;
    CPainting.mMatriceConv25[0][3] = 1 / 44f;
    CPainting.mMatriceConv25[0][4] = 1 / 44f;
    CPainting.mMatriceConv25[1][0] = 1 / 44f;
    CPainting.mMatriceConv25[1][1] = 2 / 44f;
    CPainting.mMatriceConv25[1][2] = 3 / 44f;
    CPainting.mMatriceConv25[1][3] = 2 / 44f;
    CPainting.mMatriceConv25[1][4] = 1 / 44f;
    CPainting.mMatriceConv25[2][0] = 2 / 44f;
    CPainting.mMatriceConv25[2][1] = 3 / 44f;
    CPainting.mMatriceConv25[2][2] = 4 / 44f;
    CPainting.mMatriceConv25[2][3] = 3 / 44f;
    CPainting.mMatriceConv25[2][4] = 2 / 44f;
    CPainting.mMatriceConv25[3][0] = 1 / 44f;
    CPainting.mMatriceConv25[3][1] = 2 / 44f;
    CPainting.mMatriceConv25[3][2] = 3 / 44f;
    CPainting.mMatriceConv25[3][3] = 2 / 44f;
    CPainting.mMatriceConv25[3][4] = 1 / 44f;
    CPainting.mMatriceConv25[4][0] = 1 / 44f;
    CPainting.mMatriceConv25[4][1] = 1 / 44f;
    CPainting.mMatriceConv25[4][2] = 2 / 44f;
    CPainting.mMatriceConv25[4][3] = 1 / 44f;
    CPainting.mMatriceConv25[4][4] = 1 / 44f;

    // initialisation de la matrice de convolution : lissage moyen sur 49
    // cases
    /*
     * 1 1 2 2 2 1 1 1 2 3 4 3 2 1 2 3 4 5 4 3 2 2 4 5 8 5 4 2 2 3 4 5 4 3 2 1 2
     * 3 4 3 2 1 1 1 2 2 2 1 1
     */
    CPainting.mMatriceConv49[0][0] = 1 / 128f;
    CPainting.mMatriceConv49[0][1] = 1 / 128f;
    CPainting.mMatriceConv49[0][2] = 2 / 128f;
    CPainting.mMatriceConv49[0][3] = 2 / 128f;
    CPainting.mMatriceConv49[0][4] = 2 / 128f;
    CPainting.mMatriceConv49[0][5] = 1 / 128f;
    CPainting.mMatriceConv49[0][6] = 1 / 128f;

    CPainting.mMatriceConv49[1][0] = 1 / 128f;
    CPainting.mMatriceConv49[1][1] = 2 / 128f;
    CPainting.mMatriceConv49[1][2] = 3 / 128f;
    CPainting.mMatriceConv49[1][3] = 4 / 128f;
    CPainting.mMatriceConv49[1][4] = 3 / 128f;
    CPainting.mMatriceConv49[1][5] = 2 / 128f;
    CPainting.mMatriceConv49[1][6] = 1 / 128f;

    CPainting.mMatriceConv49[2][0] = 2 / 128f;
    CPainting.mMatriceConv49[2][1] = 3 / 128f;
    CPainting.mMatriceConv49[2][2] = 4 / 128f;
    CPainting.mMatriceConv49[2][3] = 5 / 128f;
    CPainting.mMatriceConv49[2][4] = 4 / 128f;
    CPainting.mMatriceConv49[2][5] = 3 / 128f;
    CPainting.mMatriceConv49[2][6] = 2 / 128f;

    CPainting.mMatriceConv49[3][0] = 2 / 128f;
    CPainting.mMatriceConv49[3][1] = 4 / 128f;
    CPainting.mMatriceConv49[3][2] = 5 / 128f;
    CPainting.mMatriceConv49[3][3] = 8 / 128f;
    CPainting.mMatriceConv49[3][4] = 5 / 128f;
    CPainting.mMatriceConv49[3][5] = 4 / 128f;
    CPainting.mMatriceConv49[3][6] = 2 / 128f;

    CPainting.mMatriceConv49[4][0] = 2 / 128f;
    CPainting.mMatriceConv49[4][1] = 3 / 128f;
    CPainting.mMatriceConv49[4][2] = 4 / 128f;
    CPainting.mMatriceConv49[4][3] = 5 / 128f;
    CPainting.mMatriceConv49[4][4] = 4 / 128f;
    CPainting.mMatriceConv49[4][5] = 3 / 128f;
    CPainting.mMatriceConv49[4][6] = 2 / 128f;

    CPainting.mMatriceConv49[5][0] = 1 / 128f;
    CPainting.mMatriceConv49[5][1] = 2 / 128f;
    CPainting.mMatriceConv49[5][2] = 3 / 128f;
    CPainting.mMatriceConv49[5][3] = 4 / 128f;
    CPainting.mMatriceConv49[5][4] = 3 / 128f;
    CPainting.mMatriceConv49[5][5] = 2 / 128f;
    CPainting.mMatriceConv49[5][6] = 1 / 128f;

    CPainting.mMatriceConv49[6][0] = 1 / 128f;
    CPainting.mMatriceConv49[6][1] = 1 / 128f;
    CPainting.mMatriceConv49[6][2] = 2 / 128f;
    CPainting.mMatriceConv49[6][3] = 2 / 128f;
    CPainting.mMatriceConv49[6][4] = 2 / 128f;
    CPainting.mMatriceConv49[6][5] = 1 / 128f;
    CPainting.mMatriceConv49[6][6] = 1 / 128f;

    mSuspendu = false;
  }

  /**
   * Invoked when the mouse is clicked.
   * 
   * @param pMouseEvent the MouseEvent representing the mouse click event
   */
  public void mouseClicked(MouseEvent pMouseEvent) {
    pMouseEvent.consume();
    if (pMouseEvent.getButton() == MouseEvent.BUTTON1) {
      // double clic sur le bouton gauche = effacer et recommencer
      if (pMouseEvent.getClickCount() == 2) {
        init();
      }
      // simple clic = suspendre les calculs et l'affichage
      mApplis.pause();
    } else {
      // bouton du milieu (roulette) = suspendre l'affichage mais
      // continuer les calculs
      if (pMouseEvent.getButton() == MouseEvent.BUTTON2) {
        suspendre();
      } else {
        // clic bouton droit = effacer et recommencer
        // case pMouseEvent.BUTTON3:
        init();
      }
    }
  }

  /****************************************************************************/
  public void mouseEntered(MouseEvent pMouseEvent) {
  }

  /****************************************************************************/
  public void mouseExited(MouseEvent pMouseEvent) {
  }

  /****************************************************************************/
  public void mousePressed(MouseEvent pMouseEvent) {

  }

  /****************************************************************************/
  public void mouseReleased(MouseEvent pMouseEvent) {
  }

  /**
   * This method is responsible for painting the graphics on the screen.
   * It iterates over the dimensions of the graphics and fills each pixel with the
   * corresponding color.
   *
   * @param pGraphics The graphics object on which the painting is performed.
   */
  @Override
  public void paint(Graphics pGraphics) {
    int i, j;

    synchronized (mMutexCouleurs) {
      for (i = 0; i < mDimension.width; i++) {
        for (j = 0; j < mDimension.height; j++) {
          pGraphics.setColor(mCouleurs[i][j]);
          pGraphics.fillRect(i, j, 1, 1);
        }
      }
    }
  }

  /**
   * Sets the color of a pixel at the specified coordinates.
   * The color is diffused based on the given size parameter.
   *
   * @param x       the x-coordinate of the pixel
   * @param y       the y-coordinate of the pixel
   * @param c       the color to set
   * @param pTaille the size parameter for color diffusion
   */
  public void setCouleur(int x, int y, Color c, int pTaille) {
    int i, j, k, l, m, n;
    float R, G, B;
    Color lColor;

    synchronized (mMutexCouleurs) {
      if (!mSuspendu) {
        // on colorie la case sur laquelle se trouve la fourmi
        mGraphics.setColor(c);
        mGraphics.fillRect(x, y, 1, 1);
      }

      mCouleurs[x][y] = c;

      // on fait diffuser la couleur :
      switch (pTaille) {
        case 0:
          // on ne fait rien = pas de diffusion
          break;
        case 1:
          // produit de convolution discrete sur 9 cases
          for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
              R = G = B = 0f;

              for (k = 0; k < 3; k++) {
                for (l = 0; l < 3; l++) {
                  m = (x + i + k - 2 + mDimension.width) % mDimension.width;
                  n = (y + j + l - 2 + mDimension.height) % mDimension.height;
                  R += CPainting.mMatriceConv9[k][l] * mCouleurs[m][n].getRed();
                  G += CPainting.mMatriceConv9[k][l] * mCouleurs[m][n].getGreen();
                  B += CPainting.mMatriceConv9[k][l] * mCouleurs[m][n].getBlue();
                }
              }
              lColor = new Color((int) R, (int) G, (int) B);

              mGraphics.setColor(lColor);

              m = (x + i - 1 + mDimension.width) % mDimension.width;
              n = (y + j - 1 + mDimension.height) % mDimension.height;
              mCouleurs[m][n] = lColor;
              if (!mSuspendu) {
                mGraphics.fillRect(m, n, 1, 1);
              }
            }
          }
          break;
        case 2:
          // produit de convolution discrete sur 25 cases
          for (i = 0; i < 5; i++) {
            for (j = 0; j < 5; j++) {
              R = G = B = 0f;

              for (k = 0; k < 5; k++) {
                for (l = 0; l < 5; l++) {
                  m = (x + i + k - 4 + mDimension.width) % mDimension.width;
                  n = (y + j + l - 4 + mDimension.height) % mDimension.height;
                  R += CPainting.mMatriceConv25[k][l] * mCouleurs[m][n].getRed();
                  G += CPainting.mMatriceConv25[k][l] * mCouleurs[m][n].getGreen();
                  B += CPainting.mMatriceConv25[k][l] * mCouleurs[m][n].getBlue();
                }
              }
              lColor = new Color((int) R, (int) G, (int) B);
              mGraphics.setColor(lColor);
              m = (x + i - 2 + mDimension.width) % mDimension.width;
              n = (y + j - 2 + mDimension.height) % mDimension.height;

              mCouleurs[m][n] = lColor;
              if (!mSuspendu) {
                mGraphics.fillRect(m, n, 1, 1);
              }

            }
          }
          break;
        case 3:
          // produit de convolution discrete sur 49 cases
          for (i = 0; i < 7; i++) {
            for (j = 0; j < 7; j++) {
              R = G = B = 0f;

              for (k = 0; k < 7; k++) {
                for (l = 0; l < 7; l++) {
                  m = (x + i + k - 6 + mDimension.width) % mDimension.width;
                  n = (y + j + l - 6 + mDimension.height) % mDimension.height;
                  R += CPainting.mMatriceConv49[k][l] * mCouleurs[m][n].getRed();
                  G += CPainting.mMatriceConv49[k][l] * mCouleurs[m][n].getGreen();
                  B += CPainting.mMatriceConv49[k][l] * mCouleurs[m][n].getBlue();
                }
              }
              lColor = new Color((int) R, (int) G, (int) B);
              mGraphics.setColor(lColor);
              m = (x + i - 3 + mDimension.width) % mDimension.width;
              n = (y + j - 3 + mDimension.height) % mDimension.height;

              mCouleurs[m][n] = lColor;
              if (!mSuspendu) {
                mGraphics.fillRect(m, n, 1, 1);
              }

            }
          }
          break;
      }// end switch
    }
  }

  /**
   * Toggles the suspension state of the painting.
   * If the painting is currently suspended, it will be resumed and repainted.
   * If the painting is currently active, it will be suspended and not repainted.
   */
  public void suspendre() {
    mSuspendu = !mSuspendu;
    if (!mSuspendu) {
      repaint();
    }
  }
}
