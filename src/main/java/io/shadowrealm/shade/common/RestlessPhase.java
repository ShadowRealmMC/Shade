package io.shadowrealm.shade.common;

/**
 * Who sends this message
 *
 * @author cyberpwn
 */
public enum RestlessPhase
{
	/**
	 * Servers & clients can send this (meaning both clients and servers must
	 * support receiving this)
	 */
	COMMON,

	/**
	 * Only servers can send this message (meaning clients must support receiving
	 * this)
	 */
	SERVER,

	/**
	 * Only clients can send this message (meaning servers must support receiving
	 * this)
	 */
	CLIENT;
}
