/*
 * Bamana - a free incremental backup software for GNU/Linux
 * Copyright (C) 2017 Emanuele Bruni
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.ebruni.bamana;

import io.ebruni.bamana.utility.IOutilities;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class MegaTest {

	//@Test
	public void initSnapRestore() throws Exception {
		String resourceDir = Paths.get("src","test", "resources")
				.toFile()
				.getAbsolutePath();

		String archiveDir = resourceDir + "/archive";
		String sourceDir = resourceDir + "/source";
		String homeDir = resourceDir + "/home";
		String expectedLogsDir = resourceDir + "/expectedLogs";
		String logsDir = resourceDir + "/logs";

		Path logsPath = Paths.get(logsDir);
		String name = "", fileSystem = "";
		new BackupInitializer(new String[] { archiveDir, sourceDir, name, homeDir }).init();
		new Snapper(new Logger(homeDir), new String[] { sourceDir, name, homeDir, fileSystem }).snap();

		// Assuming there's only one timestamp dir.
		File timestampDir = logsPath.toFile().listFiles()[0];
		Path logPath = Paths.get(timestampDir.toPath() + "/errors/unreadable/unreadable.log");
		List<String> expectedUnreadableLog = IOutilities.readFileRows(Paths.get(expectedLogsDir + "/unreadable.log"));
		List<String> actualUnreadableLog = IOutilities.readFileRows(logPath);

		Iterator<String>  it = expectedUnreadableLog.iterator();
		System.out.println("EXPECTED (list):");
		while (it.hasNext())
			System.out.println(it.next());
		
		it = actualUnreadableLog.iterator();
		System.out.println("ACTUAL (list):");
		while (it.hasNext())
			System.out.println(it.next());
		
		assertTrue(expectedUnreadableLog.containsAll(actualUnreadableLog)
				&& actualUnreadableLog.containsAll(expectedUnreadableLog));

		// To test SnapshotRestorer, first delete the timestampDir folder,
		// then redo 'File timestampDir = logsPath.toFile().listFiles()[0];'
		// new SnapshotRestorer(new Logger(homeDir), new String[] { backupIndex,
		// restoreDir, snapshotIndex, homeDir }).restore();
	}
}