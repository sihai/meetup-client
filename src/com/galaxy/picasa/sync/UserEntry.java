/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.picasa.sync;

import com.android.gallery3d.common.Entry;
import com.android.gallery3d.common.EntrySchema;

/**
 * 
 * @author sihai
 *
 */
@Entry.Table("users")
public class UserEntry extends Entry {

	public static final EntrySchema SCHEMA = new EntrySchema(UserEntry.class);
	
	@Entry.Column(indexed=true, value="account")
	public String account;
	
	@Entry.Column("albums_etag")
	public String albumsEtag;
	
	@Entry.Column("sync_states")
	public int syncStates;

	public UserEntry() {
	}

	public UserEntry(String s) {
		account = s;
	}
}
