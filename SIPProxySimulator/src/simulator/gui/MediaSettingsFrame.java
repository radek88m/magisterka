package simulator.gui;

import javax.swing.JFrame;

import simulator.tunnel.signalling.SIPTunnelConfig;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class MediaSettingsFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	private JTextField packetDelayTextField;
	private JTextField packetLossTextField;
	private JTextField jitterTextField;
	private JTextField bandwidthTextField;
	
	SIPTunnelConfig mConfig;

	public MediaSettingsFrame(SIPTunnelConfig config) {
		
		mConfig = config;

		this.setSize(new Dimension(315, 283));
		setTitle("Media Processing Settings");
		getContentPane().setLayout(null);
		
		JLabel lblPacketDelayms = new JLabel("Packet Delay [ms]");
		lblPacketDelayms.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPacketDelayms.setBounds(16, 24, 143, 26);
		getContentPane().add(lblPacketDelayms);
		
		packetDelayTextField = new JTextField();
		packetDelayTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		packetDelayTextField.setHorizontalAlignment(SwingConstants.CENTER);
		packetDelayTextField.setText(""+mConfig.getTunnelStreamSettings().delay);
		packetDelayTextField.setBounds(163, 27, 119, 20);
		getContentPane().add(packetDelayTextField);
		packetDelayTextField.setColumns(10);
		
		JLabel lblPacketLoss = new JLabel("Packet Loss [%]");
		lblPacketLoss.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPacketLoss.setBounds(16, 67, 143, 26);
		getContentPane().add(lblPacketLoss);
		
		JLabel lblJitterSequenceRange = new JLabel("Jitter Sequence Range");
		lblJitterSequenceRange.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblJitterSequenceRange.setBounds(16, 104, 143, 26);
		getContentPane().add(lblJitterSequenceRange);
		
		JLabel lblBandwidthLimitkbps = new JLabel("Bandwidth limit [kbps]");
		lblBandwidthLimitkbps.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblBandwidthLimitkbps.setBounds(16, 146, 143, 26);
		getContentPane().add(lblBandwidthLimitkbps);
		
		packetLossTextField = new JTextField();
		packetLossTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		packetLossTextField.setText(""+mConfig.getTunnelStreamSettings().lossPercentage);
		packetLossTextField.setHorizontalAlignment(SwingConstants.CENTER);
		packetLossTextField.setColumns(10);
		packetLossTextField.setBounds(163, 70, 119, 20);
		getContentPane().add(packetLossTextField);
		
		jitterTextField = new JTextField();
		jitterTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		jitterTextField.setText(""+mConfig.getTunnelStreamSettings().jitterSequenceVariance);
		jitterTextField.setHorizontalAlignment(SwingConstants.CENTER);
		jitterTextField.setColumns(10);
		jitterTextField.setBounds(163, 107, 119, 20);
		getContentPane().add(jitterTextField);
		
		bandwidthTextField = new JTextField();
		bandwidthTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		bandwidthTextField.setText(""+mConfig.getTunnelStreamSettings().maxBandwidth);
		bandwidthTextField.setHorizontalAlignment(SwingConstants.CENTER);
		bandwidthTextField.setColumns(10);
		bandwidthTextField.setBounds(163, 149, 119, 20);
		getContentPane().add(bandwidthTextField);
		
		JButton btnNewButton = new JButton("Apply");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onApply();
			}
		});
		btnNewButton.setBounds(16, 197, 125, 32);
		getContentPane().add(btnNewButton);
		
		JButton btnClose = new JButton("Close");
		btnClose.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		});
		btnClose.setBounds(163, 197, 119, 32);
		getContentPane().add(btnClose);
		// TODO Auto-generated constructor stub
	}

	protected void onClose() {
		this.dispose();
	}

	protected void onApply() {
		try {
			int delay = Integer.parseInt(packetDelayTextField.getText());
			int loss = Integer.parseInt(packetLossTextField.getText());
			int jitter = Integer.parseInt(jitterTextField.getText());
			int bandwidth = Integer.parseInt(bandwidthTextField.getText());
			
			mConfig.getTunnelStreamSettings().delay = delay;
			mConfig.getTunnelStreamSettings().lossPercentage = loss;
			mConfig.getTunnelStreamSettings().jitterSequenceVariance = jitter;
			mConfig.getTunnelStreamSettings().maxBandwidth = bandwidth;
			
			this.dispose();	
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Invalid arguments!");
		}		
	}
}
