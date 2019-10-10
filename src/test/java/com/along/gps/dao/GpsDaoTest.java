package com.along.gps.dao;

import com.along.gps.GpsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

import static org.junit.Assert.*;
@SpringBootTest(classes =  GpsApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class GpsDaoTest {
	@Resource
	private GpsDao gpsDao;

	@Test
	public void saveGpsData() {
		System.out.println(gpsDao.getPrisoner("009"));
	}
}