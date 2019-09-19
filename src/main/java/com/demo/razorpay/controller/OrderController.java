package com.demo.razorpay.controller;

import com.demo.razorpay.RequestParameter;
import com.demo.razorpay.SessionAttributes;
import com.demo.razorpay.controller.helper.OrderControllerHelper;
import com.demo.razorpay.models.Order;
import com.demo.razorpay.models.OrderProductJoin;
import com.demo.razorpay.models.OrderTransaction;
import com.demo.razorpay.models.User;
import com.demo.razorpay.service.gateway.OrderGatewayService;
import com.demo.razorpay.service.local.OrderLocalService;
import com.demo.razorpay.service.local.OrderProductJoinLocalService;
import com.razorpay.RazorpayException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderController extends OrderControllerHelper {

    public static final String NEW = "new";
    public static final String GET = "fetch";
    public static final String SYNC = "sync";
    public static final String DETAILS = "details";
    public static final String CANCEL = "cancel";
    public static final String CANCEL_ORDER = "cancel-order";
    public static final String CONFIRM = "confirm";
    public static final String CONFIRM_ORDER = "confirm-order";
    public static final String LISTING = "listing";
    public static final String DELETE = "delete";
    private static final Logger LOGGER = Logger.getLogger(OrderController.class.getName());

    private void newOrder(HttpServletRequest request, HttpServletResponse response) {
        String orderId = request.getParameter("order-id");
        OrderTransaction orderTransaction = null;
        try {
            orderTransaction = OrderGatewayService.fetchOrderTransaction(orderId);
        } catch (RazorpayException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            toErrorPage500(request, response);
            return;
        }
        if (null != orderTransaction) {

        }
    }

    protected void doProcess(HttpServletRequest request, HttpServletResponse response) {
        String command = request.getParameter(RequestParameter.COMMAND);
        try {
            switch (command) {
                case NEW:
                    newOrder(request, response);
                case SYNC:
                    syncOrderTransactionsWithGateway(request, response);
                    break;
                case CANCEL:
                    cancelOrderTransaction(request, response);
                    break;
                case CONFIRM:
                    confirmOrderTransaction(request, response);
                    break;

                case LISTING:
                    listOrders(request, response);
                    break;
                case DELETE:
                    deleteOrder(request, response);
                    break;
                case DETAILS:
                    orderDetails(request, response);
            }
        } catch (RazorpayException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            toErrorPage500(request, response);
            return;
        }
    }

    protected void listOrders(HttpServletRequest request, HttpServletResponse response) throws RazorpayException {
        String userId = request.getParameter(RequestParameter.USER_ID);
        Order currentOrder = new Order();

        List<Order> previousOrders = OrderLocalService.listOrdersByUserId(userId);
        HttpSession httpSession = request.getSession(false);

        httpSession.setAttribute(SessionAttributes.SESSION_ORDER, currentOrder);
        httpSession.setAttribute(SessionAttributes.PREVIOUS_ORDERS, previousOrders);

        try {
            response.sendRedirect("../pages/order-listing.jsp");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            toErrorPage500(request, response);
            return;
        }
    }

    protected void deleteOrder(HttpServletRequest request, HttpServletResponse response) throws RazorpayException {
        HttpSession httpSession = request.getSession(false);
        if (null != httpSession) {
            User sessionUser = (User) httpSession.getAttribute(SessionAttributes.SESSION_USER);
            if (null != sessionUser) {
                String orderId = request.getParameter(RequestParameter.ORDER_ID);
                int result = this.deleteOrder(orderId);
                if (0 == result) {
                    try {
                        response.getWriter().print("0");
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        toErrorPage500(request, response);
                        return;
                    }
                } else {
                    try {
                        response.getWriter().print(String.format("Unable to delete the order('%s')", orderId));
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        toErrorPage500(request, response);
                        return;
                    }
                }
            } else {
                try {
                    response.sendRedirect("../pages/login.jsp");
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    toErrorPage500(request, response);
                    return;
                }
            }
        } else {
            try {
                response.sendRedirect("../pages/login.jsp");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                toErrorPage500(request, response);
                return;
            }
        }
    }

    protected void orderDetails(HttpServletRequest request, HttpServletResponse response) throws RazorpayException {
        String orderId = request.getParameter(RequestParameter.ORDER_ID);
        Order order = OrderLocalService.fetchOrderById(orderId);

        HttpSession httpSession = request.getSession(false);
        httpSession.setAttribute(SessionAttributes.SESSION_ORDER, order);

        try {
            response.sendRedirect("../pages/order-details.jsp");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            toErrorPage500(request, response);
            return;
        }
    }

    /*************************************************************/

    protected void syncOrderTransactionsWithGateway(HttpServletRequest request, HttpServletResponse response) throws RazorpayException {
        syncOrderTransactionsWithGateway();
        try {
            response.sendRedirect("../pages/list-payments.jsp");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            toErrorPage500(request, response);
            return;
        }
    }


    protected void cancelOrderTransaction(HttpServletRequest request, HttpServletResponse response) throws RazorpayException {
        String orderTransactionId = request.getParameter(RequestParameter.ORDER_TRANSACTION_ID);
        String orderTransactionDetails = cancelOrderTransaction(orderTransactionId);

        response.setContentType("text/html");
        try {
            response.getWriter().print(orderTransactionDetails);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            toErrorPage500(request, response);
            return;
        }
    }

    protected void cancelOrder(HttpServletRequest request, HttpServletResponse response) throws RazorpayException {
        String orderTransactionId = request.getParameter(RequestParameter.ORDER_TRANSACTION_ID);
        cancelOrder(orderTransactionId);

        response.setContentType("text/html");
        try {
            response.getWriter().print("Order cancelled successfully...");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            toErrorPage500(request, response);
            return;
        }
    }

    protected void confirmOrderTransaction(HttpServletRequest request, HttpServletResponse response) throws RazorpayException {
        String orderTransactionId = request.getParameter(RequestParameter.ORDER_TRANSACTION_ID);
        String orderTransactionDetails = confirmOrderTransaction(orderTransactionId);

        response.setContentType("text/html");
        try {
            response.getWriter().print(orderTransactionDetails);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            toErrorPage500(request, response);
            return;
        }
    }
}
