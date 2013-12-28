/**
 * 
 */
package soarvivor.util;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

/**
 * @author TehStoneMan
 */
public class CBFMLLoadingPlugin implements IFMLLoadingPlugin
{
	public static File	location;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cpw.mods.fml.relauncher.IFMLLoadingPlugin#getLibraryRequestClass()
	 */
	@Override
	@Deprecated
	public String[] getLibraryRequestClass()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cpw.mods.fml.relauncher.IFMLLoadingPlugin#getASMTransformerClass()
	 */
	@Override
	public String[] getASMTransformerClass()
	{
		// This will return the name of the class
		// "mod.culegooner.CBClassTransformer"
		return new String[]
		{ CBClassTransformer.class.getName() };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cpw.mods.fml.relauncher.IFMLLoadingPlugin#getModContainerClass()
	 */
	@Override
	public String getModContainerClass()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cpw.mods.fml.relauncher.IFMLLoadingPlugin#getSetupClass()
	 */
	@Override
	public String getSetupClass()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cpw.mods.fml.relauncher.IFMLLoadingPlugin#injectData(java.util.Map)
	 */
	@Override
	public void injectData(Map<String, Object> data)
	{
		// This will return the jar file of the mod "CreeperBurnCore_dummy.jar"
		location = (File) data.get("coremodLocation");
	}

}
