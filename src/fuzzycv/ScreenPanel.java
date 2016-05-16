/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzycv;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author Damilola
 */
class ScreenPanel  extends JPanel{
    
    private Image img;
    
    public ScreenPanel(String img)
    {
        this(new ImageIcon(img).getImage());
    }
    
    public ScreenPanel(Image img)
    {
    this.img = img;
    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
    setPreferredSize(size);
    setMinimumSize(size);
    setMaximumSize(size);
    setSize(size);
    setLayout(null);
    setBorder(BorderFactory.createLineBorder(Color.black));
  }

  public void updateImage(Image img)
  {
    this.img = img;
    validate();
    repaint();
  }

  @Override
  public void paintComponent(Graphics g)
  {
    g.drawImage(img, 0, 0, null);
  }
}
