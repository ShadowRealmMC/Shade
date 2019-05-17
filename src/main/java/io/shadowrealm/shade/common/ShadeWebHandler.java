package io.shadowrealm.shade.common;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ShadeWebHandler
{
	public String getNode();

	public void on(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
}
