package org.fife.ui.rsyntaxtextarea.spell.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;


/**
 * A table that lists all parser notices in a text area.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ParserNoticeTable extends JPanel implements PropertyChangeListener{

	private RSyntaxTextArea textArea;
	private JTable noticeTable;
	private DefaultTableModel model;
	public ParserNoticeTable(RSyntaxTextArea textArea) {

		setLayout(new BorderLayout());

		String[] headers = { "Description", "File", "Line" };
		model = new DefaultTableModel(headers, 0);
		noticeTable = new JTable(model);
		noticeTable.setPreferredScrollableViewportSize(new Dimension(200, noticeTable.getRowHeight()*8));
		JScrollPane sp = new JScrollPane(noticeTable);
		add(sp);

		this.textArea = textArea;
		textArea.addPropertyChangeListener(
						RSyntaxTextArea.PARSER_NOTICES_PROPERTY, this);

	}


	public void propertyChange(PropertyChangeEvent e) {

		System.out.println("Removing " + model.getRowCount() + " rows");
		int rowCount = model.getRowCount();
		for (int i=0; i<rowCount; i++) {
			model.removeRow(0);
		}

		List notices = textArea.getParserNotices();
		System.out.println("Adding " + notices.size() + " new notices");
		for (Iterator i=notices.iterator(); i.hasNext(); ) {
			ParserNotice notice = (ParserNotice)i.next();
			String[] data = { notice.getMessage(), "Foo.java", Integer.toString(notice.getLine()) };
			model.addRow(data);
			System.out.println("... new row count: " + model.getRowCount());
		}

	}


}