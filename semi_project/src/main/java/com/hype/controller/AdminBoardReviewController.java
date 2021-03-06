package com.hype.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.hype.dao.AdminBoardReviewDAO;
import com.hype.dto.ImageDTO;
import com.hype.dto.ReviewDTO;

@WebServlet("*.rv")
public class AdminBoardReviewController extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doAction(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doAction(request, response);
	}

	protected void doAction(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html ; charset=utf-8");

		String uri = request.getRequestURI();
		System.out.println("요청 uri : " + uri);

		if (uri.equals("/review.rv")) { // 리뷰 조회 페이지로 이동
			AdminBoardReviewDAO dao = new AdminBoardReviewDAO();
			int curPage = Integer.parseInt(request.getParameter("curPage"));

			try {

				// 전체 리뷰 데이터를 list화 시킴
				ArrayList<ReviewDTO> list = dao.selectAll(curPage * 10 - 9, curPage * 10);
				request.setAttribute("list", list);

				// 페이지네이션 HashMap을 데이터화 시킴
				HashMap map = dao.getPageNavi(curPage);
				request.setAttribute("naviMap", map);
				request.setAttribute("curPage", curPage);
				request.getRequestDispatcher("/admin/board/boardReviewManage.jsp").forward(request, response);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (uri.equals("/checkReviewDetail.rv")) {
			AdminBoardReviewDAO dao = new AdminBoardReviewDAO();

			int seq_review = Integer.parseInt(request.getParameter("seq_review"));
			int seq_product = Integer.parseInt(request.getParameter("seq_product"));

			try {
				ArrayList<ReviewDTO> reviewList = dao.selectContentBy(seq_review);
				ArrayList<ImageDTO> imageList = dao.selectImageBy(seq_product);
				ArrayList<Object> listAll = new ArrayList<Object>();
				listAll.addAll(reviewList);
				listAll.addAll(imageList);
				
				// ajax 이용
				Gson gson = new Gson();
				String rs = gson.toJson(listAll);
				response.setCharacterEncoding("utf-8");
				response.getWriter().append(rs);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (uri.equals("/deleteReviewDetail.rv")) {

			int seq_review = Integer.parseInt(request.getParameter("seq_review"));
			AdminBoardReviewDAO dao = new AdminBoardReviewDAO();
			try {
				int rs = dao.delete(seq_review);
				if (rs > 0) {
					System.out.println("해당 리뷰가 삭제되었습니다");
					int curPage = 1;

					ArrayList<ReviewDTO> list = dao.selectAll(curPage * 10 - 9, curPage * 10);
					request.setAttribute("list", list);

					HashMap map = dao.getPageNavi(curPage);
					request.setAttribute("naviMap", map);

					// ajax 이용
					Gson gson = new Gson();
					String dataList = gson.toJson(list);
					response.setCharacterEncoding("utf-8");
					response.getWriter().append(dataList);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (uri.equals("/searchProc.rv")) { // 해당 키워드로 검색
			
			String searchKeyword = request.getParameter("searchKeyword"); // 해당 키워드(배송 상태로 검색한 결과)
			int curPage = 1;
			AdminBoardReviewDAO dao = new AdminBoardReviewDAO();

			try {

				ArrayList<ReviewDTO> list = dao.searchByKeyword(searchKeyword, curPage * 10 - 9, curPage * 10);
				request.setAttribute("list", list);

				HashMap map = dao.searchedGetPageNavi(curPage, searchKeyword);
				request.setAttribute("naviMap", map);

				request.getRequestDispatcher("/admin/board/boardReviewManageSearch.jsp").forward(request, response);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
