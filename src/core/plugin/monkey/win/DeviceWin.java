package core.plugin.monkey.win;

import com.intellij.ui.content.Content;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import core.plugin.monkey.WinFactory;
import core.plugin.monkey.core.Device;
import core.plugin.monkey.core.Monkey;
import core.plugin.monkey.log.TextPrinter;
import core.plugin.monkey.win.base.BaseWin;
import core.plugin.monkey.win.device.BuilderDlg;

/**
 * @author DrkCore
 * @since 2017-01-26
 */
public class DeviceWin extends BaseWin {
    
    private JPanel contentPanel;
    private JButton runBtn;
    private JButton stopBtn;
    private JScrollPane scrollPanel;
    private JTextArea logTextArea;
    private JButton settingBtn;
    private JButton clearBtn;
    private JCheckBox scrollCheckBox;
    
    public static final String TAG_DEVICE = "Device:";
    private final Device device;
    private TextPrinter logPrinter;
    
    @Override
    public JPanel getContentPanel() {
        return contentPanel;
    }
    
    @Override
    public void onAttached(WinFactory factory, Content content) {
        super.onAttached(factory, content);
        content.setCloseable(true);
    }
    
    public DeviceWin(String device) {
        super(TAG_DEVICE + device);
        
        logPrinter = new TextPrinter(logTextArea);
        
        this.device = new Device(device);
        this.device.setListener(logPrinter);
        
        runBtn.addActionListener(e -> startMonkey(false));
        stopBtn.addActionListener(e -> this.device.terminal());
        settingBtn.addActionListener(e -> startMonkey(true));
        clearBtn.addActionListener(e -> logPrinter.clearLog());
        
        scrollCheckBox.addChangeListener(e -> {
            System.out.println(scrollCheckBox.isSelected());
            logPrinter.setAutoScroll(scrollCheckBox.isSelected());
        });
    }
    
    /*运行*/
    
    private Monkey.Builder config;
    
    private void startMonkey(boolean needConfig) {
        if (config == null) {
            needConfig = true;
        }
        
        if (needConfig) {
            new BuilderDlg(config).setCallback((config) -> {
                DeviceWin.this.config = config;
                doRun(config.build());
            }).show();
        } else {
            doRun(config.build());
        }
    }
    
    private void doRun(Monkey monkey) {
        ConsoleWin console = getFactory().getConsoleWin();
        console.log("Monkey: " + monkey.getCmd());
        console.log("Infinity: " + monkey.isInfinite());
        console.log("Device: " + device.getDevice());
        console.log("_____________________________________\n");
        
        device.submit(monkey);
    }
    
}
