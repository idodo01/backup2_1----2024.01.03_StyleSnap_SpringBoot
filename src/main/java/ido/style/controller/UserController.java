package ido.style.controller;

import ido.style.dto.CartDTO;
import ido.style.dto.UserDTO;
import ido.style.service.OrderService;
import ido.style.service.PortOneService;
import ido.style.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LogManager.getLogger(UserController.class);
    @Autowired private UserService userService;
    @Autowired private OrderService orderService;
    @Autowired private PortOneService portOneService;
    
    @GetMapping("/login")
    public String get_login(Authentication authentication) {
        // 이미 로그인이 되어 있는 상태이다
        if(Objects.nonNull(authentication)){
            return "redirect:/";
        }
        return "user/login";
    }
    /************************************************/
    @GetMapping("/join")
    public String get_join(@ModelAttribute UserDTO userDTO, Authentication authentication) {
        // 이미 로그인이 되어 있는 상태이다
        if(Objects.nonNull(authentication)){
            return "redirect:/";
        }
        return "user/join";
    }

    @PostMapping("/join")
    public String post_join(
            @ModelAttribute @Validated UserDTO userDTO,
            BindingResult bindingResult,
            HttpSession session
    ) {
        if(bindingResult.hasErrors()){
            log.error("에러 발생!");
            log.error(bindingResult.getAllErrors());
            return "user/join";
        }

        userDTO.setCi("TEST_CI2");

        boolean joinResult = userService.join_user(userDTO);
        // 가입 성공이면 login 화면으로, 실패라면 회원가입 화면으로.
        return joinResult ? "redirect:/user/login" : "user/join";
    }
    /************************ 장바구니 및 주문 ***************************/
    @GetMapping("/cart")
    public void get_user_cart(
            Model model,
            @AuthenticationPrincipal UserDTO user
    ){
        Integer totalPrice = 0;

        if(Objects.nonNull(user)){
            List<CartDTO> carts = orderService.get_carts_by_user(user);
            totalPrice = orderService.calculate_total_price(carts);
            model.addAttribute("carts", carts);
        }

        model.addAttribute("totalPrice", totalPrice);
    }

    /************************ 유저 정보 ***************************/
    @GetMapping("/info")
    public void get_user_info(
            @AuthenticationPrincipal UserDTO user,
            Model model
    ){
//        List<OrderDTO> orders = orderService.get_orders_by_user(user.getId());
////        log.info("조회된 ORDER: " + orders);
//        model.addAttribute("orders", orders);
    }

}
