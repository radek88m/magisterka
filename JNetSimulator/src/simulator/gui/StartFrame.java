package simulator.gui;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JTextField;

import simulator.tunnel.signalling.SIPTunnelConfig;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class StartFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JTextField sipServerTextField;
	private JTextField localSipPortTextField;
	private JTextField portRangeTextField;
	
	private JButton btnStart;
	
	public StartFrame() {
		setTitle("SIP Media Proxy Simulator");
		
		this.setSize(new Dimension(400, 235));
		
		JLabel lblSipServer = new JLabel("SIP Server");
		lblSipServer.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JLabel lblLocalSipListener = new JLabel("Local SIP listener port");
		lblLocalSipListener.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JLabel lblLocalMediaPort = new JLabel("Local Media Port Range");
		lblLocalMediaPort.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		btnStart = new JButton("START");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				handleStartButton();
			}
		});
		btnStart.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		sipServerTextField = new JTextField();
		sipServerTextField.setText("192.168.0.108:5060");
		sipServerTextField.setColumns(10);
		
		localSipPortTextField = new JTextField();
		localSipPortTextField.setText("666");
		localSipPortTextField.setColumns(10);
		
		portRangeTextField = new JTextField();
		portRangeTextField.setText("20000:30000");
		portRangeTextField.setColumns(10);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblSipServer, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
							.addComponent(sipServerTextField, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblLocalMediaPort, GroupLayout.PREFERRED_SIZE, 125, Short.MAX_VALUE)
								.addComponent(lblLocalSipListener, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(localSipPortTextField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
								.addComponent(portRangeTextField, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE))))
					.addGap(21))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSipServer, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
						.addComponent(sipServerTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblLocalSipListener, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
						.addComponent(localSipPortTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblLocalMediaPort, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
						.addComponent(portRangeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(28, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);
	}

	protected void handleStartButton() {
		
		try {
			String sipServerDomain = sipServerTextField.getText().split(":")[0];
			int sipServerPort = Integer.parseInt(sipServerTextField.getText().split(":")[1]);
			int localSipPort = Integer.parseInt(localSipPortTextField.getText());
			int mediaPortbegin = Integer.parseInt(portRangeTextField.getText().split(":")[0]);
			int mediaPortEnd = Integer.parseInt(portRangeTextField.getText().split(":")[1]);
			
			SIPTunnelConfig mConfig = new SIPTunnelConfig(sipServerDomain, sipServerPort, localSipPort);
			mConfig.setStreamPortRange(mediaPortbegin, mediaPortEnd);
			
			SIPTunnelFrame frame = new SIPTunnelFrame(mConfig);
			frame.setDefaultCloseOperation(StartFrame.EXIT_ON_CLOSE);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			
			this.setVisible(false);			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Invalid arguments!");
		}		
	}
}
