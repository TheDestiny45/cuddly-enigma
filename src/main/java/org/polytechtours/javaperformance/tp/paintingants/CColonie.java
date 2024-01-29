package org.polytechtours.javaperformance.tp.paintingants;

import java.util.LinkedList;

/**
 * Represents a colony of ants.
 * 
 * This class implements the Runnable interface and is responsible for
 * simulating the behavior of a colony of ants.
 * It contains methods to start and stop the simulation, as well as the main
 * logic for moving the ants and updating the counter.
 */
public class CColonie implements Runnable {

  private Boolean mContinue = Boolean.TRUE;
  private LinkedList<CFourmi> mColonie;
  private PaintingAnts mApplis;

  /**
   * Creates a new instance of CColonie.
   * 
   * @param pColonie the list of ants in the colony
   * @param pApplis  the instance of the PaintingAnts application
   */
  public CColonie(LinkedList<CFourmi> pColonie, PaintingAnts pApplis) {
    mColonie = pColonie;
    mApplis = pApplis;
  }

  /**
   * Stops the execution of the colony.
   */
  public void pleaseStop() {
    mContinue = false;
  }

  /**
   * Runs the colony simulation.
   * 
   * This method is responsible for executing the main logic of the colony
   * simulation.
   * It continuously moves the ants in the colony and updates the counter in the
   * application.
   * The simulation continues until the 'mContinue' flag is set to false or the
   * application is paused.
   */
  @Override
  public void run() {

    while (mContinue == true) {
      if (!mApplis.getPause()) {
        for (int i = 0; i < mColonie.size(); i++) {
          mColonie.get(i).deplacer();
          mApplis.compteur();
        }
      } else {
        /*
         * try { Thread.sleep(100); } catch (InterruptedException e) { break; }
         */

      }
    }
  }

}
