/*
 * FlightDrop.java
 *
 * Created on April 8, 2008, 4:59 PM
 */

package sidnet.models.deployment.models.flightdrop;

import jist.runtime.JistAPI;
import jist.swans.Constants;
import jist.swans.field.Placement;
import jist.swans.misc.Location;
import sidnet.core.gui.SimGUI;
import sidnet.models.deployment.interfaces.DeploymentModel;

/**
 * 
 * @author  Oliver
 */
public class FlightDrop extends javax.swing.JFrame implements DeploymentModel, JistAPI.DoNotRewrite
{
    private Placement placement = null;
    
    private boolean deployCommandSubmitted = false;
    
    /** placement boundaries. */
    private int x = 5000, y = 5000;
    
    // plane hieght in field units [m]!!!
    private float planeHeight = 100;
    
    private float earthG = 9.80665f; // [m/s^2]
    
    private float randomMagnitude = 500;  
    
    private float nodeMass = 400; // [grams]
    
    /* number of nodes to be thrown uniformly in time during flight */
    private int numNodes = 500;
    
    private float currPlaneX = 0, currPlaneY = 0;
    private float finalPlaneX = (float)x, finalPlaneY = (float)y;
    
    /* in feet per second */
    private float currPlaneVx = 100, currPlaneVy = 100;
    
    private float windVx = 0, windVy = 0;

    /**
     * Initialize flight drop placement model.
     *
     * @param x x-axis upper limit
     * @param y y-axis upper limit
     */
    public FlightDrop(Integer x, Integer y, SimGUI simGUI /* unused */)
    {
        initComponents();
        this.x = x; this.y = y; 
        refreshControls();
        this.setVisible(true);
        this.setEnabled(true);
    }

    /**
     * Refresh the GUI flight deployment parameters
     *
     */
    private void refreshControls()
    {
        // Field Parameters
        jTextFieldLength.setText("" + x);
        jTextFieldWidth.setText(""+ y);
        jTextFieldNumberOfNodes.setText("" + numNodes);
        jTextFieldNodeMass.setText("" + nodeMass);
        jTextFieldGravitationalAcceleration.setText("" + earthG);
        jTextFieldRandomMagnitude.setText("" + randomMagnitude);
        
        // Plane parameters
        jTextFieldStartXLocation.setText("" + currPlaneX);
        jTextFieldStartYLocation.setText("" + currPlaneY);
        jTextFieldStopXLocation.setText("" + finalPlaneX);
        jTextFieldStopYLocation.setText("" + finalPlaneY);
        jTextFieldPlaneHeight.setText("" + planeHeight);
        
        // Wind parameters
        jTextFieldWindSpeedXcoord.setText("" + windVx);
        jTextFieldWindSpeedYcoord.setText("" + windVy);
    }
    
    /**
     * Creates a copy of the resulting FlighDropPlacement object
     * @return FlightPlacement Object
     */
    public Placement getPlacement()
    {
        while(!deployCommandSubmitted) // to avoid using a callback method
        {
            try{
                Thread.sleep(500);
            }catch(Exception e){e.printStackTrace();};
        }
       
        x = Integer.parseInt(jTextFieldLength.getText());
        y = Integer.parseInt(jTextFieldWidth.getText());
        numNodes = Integer.parseInt(jTextFieldNumberOfNodes.getText());
        planeHeight = Float.parseFloat(jTextFieldPlaneHeight.getText());
        earthG = Float.parseFloat(jTextFieldGravitationalAcceleration.getText());
        currPlaneX = Float.parseFloat(jTextFieldStartXLocation.getText());
        currPlaneY = Float.parseFloat(jTextFieldStartYLocation.getText());
        finalPlaneX = Float.parseFloat(jTextFieldStopXLocation.getText());
        finalPlaneY = Float.parseFloat(jTextFieldStopYLocation.getText());
        randomMagnitude = Float.parseFloat(jTextFieldRandomMagnitude.getText());
        windVx = Float.parseFloat(jTextFieldWindSpeedXcoord.getText());
        windVy = Float.parseFloat(jTextFieldWindSpeedYcoord.getText());
        
        this.dispose();
        
        return new FlightDropPlacement(x, y,
                                       numNodes, planeHeight, earthG,
                                       currPlaneX, currPlaneY, finalPlaneX, finalPlaneY,
                                       randomMagnitude, 
                                       windVx, windVy);
    }
    
    
   /**
    * A FlighDropPlacement class that provides a very basic simulation of the nodes 
    * being dropped off a plane.
    */
   public class FlightDropPlacement implements Placement
    {
        /** placement boundaries. */
        private int x = 5000, y = 5000;

        // plane hieght in field units [m]!!!
        private float planeHeight = 1000;

        // 9.8 in m/s^2
        private float earthG = 9.80665f; // [m/s^2]

        private float randomMagnitude = 100;  

        private float nodeMass = 400; // [grams]

        /* number of nodes to be thrown uniformly in time during flight */
        private int numNodes = 500;

        private float currPlaneX = 0, currPlaneY = 0;
        private float finalPlaneX = x, finalPlaneY = y;

        /* in field units per second */
        private float currPlaneVx = 100, currPlaneVy = 100;

        private float windVx = 0, windVy = 0;
      
        /**
         * FlightPlacementConstructor
         * @param x horizontal size of field in feet
         * @param y vertical size of field in feet
         * @param numNodes the number of nodes to drop
         * @param planeHeight the altitude of the plane in feet
         * @param earthG acceleration in feet
         * @param currPlaneX initial plane x-coordinate
         * @param currPlaneY initial plane y-coordinate
         * @param finalPlaneX final plane x-coordinate
         * @param finalPlaneY final plane y-coordinate
         * @param randomMagnitude magnitude of velocity vector of nodes when
         * they leave the plane
         * @param windVx x-component of wind speed [m]
         * @param windVy y-component of wind speed [m]
         */
        public FlightDropPlacement(int x, int y,
                          int numNodes, float planeHeight, float earthG,
                          float currPlaneX, float currPlaneY, float finalPlaneX, float finalPlaneY,
                          float randomMagnitude, 
                          float windVx, float windVy)
        {
              this.x = x;
              this.y = y;

              this.numNodes = numNodes;

              this.planeHeight = planeHeight;

              this.earthG = earthG;

              this.currPlaneX = currPlaneX;
              this.currPlaneY = currPlaneY;

              this.finalPlaneX = finalPlaneX;
              this.finalPlaneY = finalPlaneY;

              currPlaneVx = (finalPlaneX - currPlaneX) / numNodes;
              currPlaneVy = (finalPlaneY - currPlaneY) / numNodes;

              this.randomMagnitude = randomMagnitude;
              this.windVx = windVx;
              this.windVy = windVy;
        }
        
        /**
         * 
         * @return integer the number of nodes the placement drops
         */
        public int getSize()
        {
            return numNodes;
        }

        //////////////////////////////////////////////////
        // Placement interface
        //

        /**
         * @return a Location object representing the next place a node has
         * fallen
         */
        public  Location getNextLocation()
        {
          float finalNodeX, finalNodeY;
          float initialNodeX, initialNodeY;
          float initialNodeVx, initialNodeVy;
          float time;

          currPlaneX = currPlaneX + currPlaneVx;
          currPlaneY = currPlaneY + currPlaneVy;

          initialNodeX = currPlaneX;
          initialNodeY = currPlaneY;
          initialNodeVx = currPlaneVx;
          initialNodeVy = currPlaneVy;

          // thus max initial speed is randomMagnitude * sqrt(2)
          initialNodeVx += Constants.random.nextFloat() * randomMagnitude;
          initialNodeVy += Constants.random.nextFloat() * randomMagnitude;

          initialNodeVx += windVx;
          initialNodeVy += windVy;


          time = (float) Math.sqrt((2 * planeHeight) / earthG);

          finalNodeX = initialNodeX + (initialNodeVx * time);
          finalNodeY = initialNodeY + (initialNodeVy * time);

          if (finalNodeX < 0) finalNodeX = 0;
          if (finalNodeX >= x) finalNodeX = x - 1;
          if (finalNodeY < 0) finalNodeY = 0;
          if (finalNodeY >= y) finalNodeY = y - 1;

          System.out.println("coords: " + finalNodeX + ", " + finalNodeY + "\n");
          return new Location.Location2D(finalNodeX, finalNodeY);
        }
    }
  
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldStartXLocation = new javax.swing.JTextField();
        jTextFieldStartYLocation = new javax.swing.JTextField();
        jTextFieldStopXLocation = new javax.swing.JTextField();
        jTextFieldStopYLocation = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldPlaneHeight = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextFieldWindSpeedXcoord = new javax.swing.JTextField();
        jTextFieldWindSpeedYcoord = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jButtonDeploy = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldLength = new javax.swing.JTextField();
        jTextFieldWidth = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldNodeMass = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jTextFieldGravitationalAcceleration = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jTextFieldRandomMagnitude = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldNumberOfNodes = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FlightDrop Deployment Model v1.0");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(128, 128, 128));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(98, 98, 98)
                .add(jLabel27)
                .addContainerGap(158, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(88, 88, 88)
                .add(jLabel27)
                .addContainerGap(113, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Airplane Parameters"));

        jLabel6.setText("Start X-location");

        jLabel7.setText("Start Y-location");

        jLabel8.setText("Stop X-location");

        jLabel9.setText("Stop Y-location");

        jTextFieldStartXLocation.setText("jTextField1");

        jTextFieldStartYLocation.setText("jTextField1");

        jTextFieldStopXLocation.setText("jTextField1");

        jTextFieldStopYLocation.setText("jTextField1");

        jLabel3.setText("Plane Height");

        jTextFieldPlaneHeight.setText("jTextField1");

        jLabel14.setText("[m]");

        jLabel15.setText("[m]");

        jLabel16.setText("[m]");

        jLabel17.setText("[m]");

        jLabel20.setText("[m]");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6)
                    .add(jLabel7)
                    .add(jLabel8)
                    .add(jLabel9)
                    .add(jLabel3))
                .add(40, 40, 40)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jTextFieldPlaneHeight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel20))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jTextFieldStartYLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel15))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jTextFieldStartXLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel14))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jTextFieldStopXLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel16))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jTextFieldStopYLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel17)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(jTextFieldStartXLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel14))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(jTextFieldStartYLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel15))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(jTextFieldStopXLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel16))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(jTextFieldStopYLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel17))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jTextFieldPlaneHeight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel20))
                .add(40, 40, 40))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Wind Parameters"));

        jLabel12.setText("Speed along X-coord");

        jLabel13.setText("Speed along Y-coord");

        jTextFieldWindSpeedXcoord.setText("jTextField1");

        jTextFieldWindSpeedYcoord.setText("jTextField1");

        jLabel21.setText("[m]");

        jLabel22.setText("[m]");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel12)
                    .add(jLabel13))
                .add(13, 13, 13)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextFieldWindSpeedYcoord, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextFieldWindSpeedXcoord, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel21)
                    .add(jLabel22))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(jTextFieldWindSpeedXcoord, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel21))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(jTextFieldWindSpeedYcoord, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel22)))
        );

        jButtonDeploy.setText("Deploy");
        jButtonDeploy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonDeployMouseClicked(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Field Parameters"));

        jLabel1.setText("Field Length");

        jLabel2.setText("Field Width [m]");

        jTextFieldLength.setText("jTextField1");
        jTextFieldLength.setAutoscrolls(false);

        jTextFieldWidth.setText("jTextField1");
        jTextFieldWidth.setAutoscrolls(false);

        jLabel4.setText("Node Mass");

        jTextFieldNodeMass.setText("jTextField1");
        jTextFieldNodeMass.setAutoscrolls(false);

        jLabel23.setText("[m]");

        jLabel24.setText("[m]");

        jLabel25.setText("[grams]");

        jLabel26.setText("Gravitational Acc.");

        jTextFieldGravitationalAcceleration.setText("jTextField1");
        jTextFieldGravitationalAcceleration.setAutoscrolls(false);

        jLabel28.setText("[ft/s^2]");

        jLabel29.setText("Random Magnitude");

        jTextFieldRandomMagnitude.setText("jTextField1");
        jTextFieldRandomMagnitude.setAutoscrolls(false);

        jLabel5.setText("Number of Nodes");

        jTextFieldNumberOfNodes.setText("jTextField1");
        jTextFieldNumberOfNodes.setAutoscrolls(false);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel2)
                    .add(jLabel4)
                    .add(jLabel5)
                    .add(jLabel26)
                    .add(jLabel29))
                .add(11, 11, 11)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jTextFieldGravitationalAcceleration, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel28))
                    .add(jTextFieldRandomMagnitude, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextFieldNumberOfNodes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jTextFieldNodeMass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel25))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jTextFieldWidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel24))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jTextFieldLength, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel23)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextFieldLength, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel23))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jTextFieldWidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel24))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(jTextFieldNumberOfNodes, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jTextFieldNodeMass, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel25))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextFieldGravitationalAcceleration, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel26)
                    .add(jLabel28))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel29)
                    .add(jTextFieldRandomMagnitude, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(26, 26, 26))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .add(51, 51, 51)
                        .add(jButtonDeploy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(25, 25, 25)
                        .add(jButtonDeploy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDeployMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jButtonDeployMouseClicked
    {//GEN-HEADEREND:event_jButtonDeployMouseClicked
        // TODO add your handling code here:
        deployCommandSubmitted = true;
        this.dispose();
    }//GEN-LAST:event_jButtonDeployMouseClicked
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new FlightDrop(5000, 5000, null).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDeploy;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jTextFieldGravitationalAcceleration;
    private javax.swing.JTextField jTextFieldLength;
    private javax.swing.JTextField jTextFieldNodeMass;
    private javax.swing.JTextField jTextFieldNumberOfNodes;
    private javax.swing.JTextField jTextFieldPlaneHeight;
    private javax.swing.JTextField jTextFieldRandomMagnitude;
    private javax.swing.JTextField jTextFieldStartXLocation;
    private javax.swing.JTextField jTextFieldStartYLocation;
    private javax.swing.JTextField jTextFieldStopXLocation;
    private javax.swing.JTextField jTextFieldStopYLocation;
    private javax.swing.JTextField jTextFieldWidth;
    private javax.swing.JTextField jTextFieldWindSpeedXcoord;
    private javax.swing.JTextField jTextFieldWindSpeedYcoord;
    // End of variables declaration//GEN-END:variables
    
}
